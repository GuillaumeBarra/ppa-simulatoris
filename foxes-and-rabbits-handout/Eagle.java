import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Write a description of class Eagle here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Eagle extends Animal
{
    // Characteristics shared by all eagles (class variables).

    // The probability of an eagle catching a prey successfully.
    private static double huntingProbability = 0.3;
    // The probability change of an eagle catching a prey successfully.
    private static final double HUNTING_PROBABILITY_CHANGE = -0.2;
    // The age at which an eagle can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which an eagle can live.
    private static final int MAX_AGE = 100;
    // The likelihood of an eagle breeding when it meets another rabbit
    private static final double BREEDING_PROBABILITY = 0.15;
    // The number of years before an eagle can breed again.
    private static final int BREEDING_INTERVAL = 9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps an eagle can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    // Wether or not an eagle is asleep.
    private  boolean isAsleep = false;
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.2;
    
    private static final double EAGLE_CREATION_PROBABILITY = 0.08;

    // Individual characteristics (instance fields).
    // an eagle's age.
    private int age;
    // The age of an eagle when it last bred.
    private int ageLastBred;
    // An eagle's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create an eagle. An eagle can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the eagle will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Eagle(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }

    /**
     * This is what the eagle does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newEagles A list to return newly born eagles.
     */
    public void act(List<Organism> newEagles, boolean isNight)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (!isNight) {
                isAsleep = false;
            }
            if (isAsleep) {
                return;
            }
            else {
                if(isNight){
                    isAsleep = rand.nextDouble() <= SLEEP_PROBABILITY ? true : false;
                    if (isAsleep) {
                        return;
                    }
                    huntingProbability += HUNTING_PROBABILITY_CHANGE;
                }
                breed(newEagles); 
                // Move towards a source of food if found.
                Location newLocation = findFood(isNight);
                if(newLocation == null) { 
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }
    
    private static double getCreationProbability(){
        return EAGLE_CREATION_PROBABILITY;
    }

    /**
     * Increase the age. This could result in the eagle's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Make this eagle more hungry. This could result in the eagle's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(boolean isNight)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    if (rand.nextDouble() <= huntingProbability) {
                        if (rand.nextDouble() <= rabbit.getEscapeProbability(isNight)) { // NOTE: review this.
                            rabbit.setDead();
                            foodLevel = RABBIT_FOOD_VALUE;
                            return where;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this eagle is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newEagles A list to return newly born eagles.
     */
    public void breed(List<Organism> newEagles)
    {
        // New eagles are born into adjacent locations.
        // Get a list of adjacent free locations.
        String sex = getSex();
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> locations = field.adjacentLocations(getLocation());
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Eagle){
                Eagle eagle = (Eagle) animal;
                if (eagle.getSex() != sex){
                    if (rand.nextDouble() <= BREEDING_PROBABILITY) {
                        int births = rand.nextInt(MAX_LITTER_SIZE) + 1;
                        for(int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Eagle young = new Eagle(false, field, loc);
                            newEagles.add(young);
                        }
                    }
                }
            }
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * An eagle can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return (age >= BREEDING_AGE) && (age >= ageLastBred + BREEDING_INTERVAL);
    }
}

