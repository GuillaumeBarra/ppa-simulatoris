import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of an eagle.
 * Eagles sleep, age, move, eat preys, and die.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Eagle extends Animal
{
    // Characteristics shared by all eagles (class variables).
    // A shared random number generator to control procreateing.
    private static final Random rand = Randomizer.getRandom();
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.5;
    // The age at which an eagle can start to procreate.
    private static final int PROCREATING_AGE = 5;
    // The age to which an eagle can live.
    private static final int MAX_AGE = 60;
    // The likelihood of an eagle procreateing when it meets another rabbit
    private static final double PROCREATING_PROBABILITY = 0.08;
    // The number of years before an eagle can procreate again.
    private static final int PROCREATING_INTERVAL = 9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The probability of an eagle catching a prey successfully.
    private static double huntingProbability = 0.4;
    // The probability change of an eagle catching a prey successfully when it's night.
    private static final double HUNTING_PROBABILITY_CHANGE = -0.05;
    // The food value of a single rabbit.
    private static final int RABBIT_FOOD_VALUE = 9;
    // The food value of a single iguana.
    private static final int IGUANA_FOOD_VALUE = 12;

    // Individual characteristics (instance fields).
    // an eagle's age.
    private int age;
    // The age of an eagle when it last bred.
    private int ageLastBred;
    // An eagle's food level, which is increased by eating rabbits.
    private int foodLevel;
    // Wether or not an eagle is asleep.
    private  boolean isAsleep = false;

    /**
     * Create an eagle. An eagle can be created as a newborn (age zero,
     * with a full stomach, and awake) or with a random age, food level,
     * and it could be awake or asleep.
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
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE + IGUANA_FOOD_VALUE);
            isAsleep = rand.nextBoolean();
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE + IGUANA_FOOD_VALUE;
            isAsleep = false;
        }
    }

    /**
     * This is what the eagle does most of the time: it hunts for
     * rabbits and iguanas. In the process, it might sleep, procreate, die of hunger,
     * or die of old age.
     * 
     * @param newEagles A list to return newly born eagles.
     * @param isNight Whether it is night time.
     */
    public void act(List<Organism> newEagles, boolean isNight)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()){
            if (!isNight) {
                // Animals don't sleep during daytime.
                isAsleep = false;
            }
            if (isAsleep) {
                // If the animal is already aleep, do nothing.
                return;
            }
            else {
                if(isNight){
                    // Run a probability check to determine whether the animal sleeps.
                    isAsleep = rand.nextDouble() <= SLEEP_PROBABILITY ? true : false;
                    if (isAsleep) {
                        return;
                    }
                }
                if (canProcreate()){
                    procreate(newEagles);
                }
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
    
    /**
     * Look for rabbits and iguanas adjacent to the current location.
     * Only the first alive prey is eaten.
     * 
     * @param isNight Whether it is night time.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(boolean isNight)
    {
        double tempHuntingProbability;
        if (isNight){
            tempHuntingProbability = huntingProbability + HUNTING_PROBABILITY_CHANGE;
        } else {
            tempHuntingProbability = huntingProbability;
        }
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    if (rand.nextDouble() <= tempHuntingProbability) {
                        if (rand.nextDouble() <= rabbit.getEscapeProbability(isNight)) {
                            rabbit.setDead();
                            foodLevel = RABBIT_FOOD_VALUE;
                            return where;
                        }
                    }
                }
            } else if(animal instanceof Iguana) {
                Iguana iguana = (Iguana) animal;
                if(iguana.isAlive()) { 
                    if (rand.nextDouble() <= tempHuntingProbability) {
                        if (rand.nextDouble() <= iguana.getEscapeProbability(isNight)) {
                            iguana.setDead();
                            foodLevel = IGUANA_FOOD_VALUE;
                            return where;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Try to procreate.
     * For all adjacent locations, if any of them is an Eagle of the opposite sex, try to mate witht them.
     * Newborns are placed in free adjacent locations.
     * 
     * @param newEagles A list of newly born eagles.
     */
    public void procreate(List<Organism> newEagles)
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
                    if (rand.nextDouble() <= PROCREATING_PROBABILITY) {
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
     * Increase age by one.
     * If age goes above the organism's max age, it dies.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Increage hunger.
     * If hunger goes below one, eagle dies.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    /**
     * Get the hunting probability.
     * 
     * @return huntingProbability.
     */
    public static double getHuntingProbability()
    {
        return huntingProbability;
    }

    /**
     * Change the hunting probability.
     * 
     * @param newHuntingProbability The new value of huntingProbability.
     */
    public static void setHuntingProbability(double newHuntingProbability)
    {
        assert newHuntingProbability >= 0 : "Eagle hunting probability below zero!!" + newHuntingProbability;
        huntingProbability = newHuntingProbability;
    }

    /**
     * A eagle can procreate if it has reached the procreateing age,
     * or if it hasn't procreated in its procreating interval.
     * 
     * @return true if the fox can procreate, false otherwise.
     */
    private boolean canProcreate()
    {
        return (age >= PROCREATING_AGE) && (age >= ageLastBred + PROCREATING_INTERVAL);
    }
}

