import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits sleep, eat, age, move, procreate, and die.
 * 
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).
    // A shared random number generator to control procreateing.
    private static final Random rand = Randomizer.getRandom();
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.5;
    // The probability of a rabbit escaping from a predator.
    private static double escapeProbability = 0.5;
    // The probability change of a rabbit escaping from a predator.
    private static final double ESCAPE_PROBABILITY_CHANGE = 0.1;
    // The age at which a rabbit can start to procreate.
    private static final int PROCREATING_AGE = 1;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 15;
    // The likelihood of a rabbit procreateing when it meets another rabbit.
    private static final double PROCREATING_PROBABILITY = 0.15;
    // The number of years before a rabbit can procreate again.
    private static final int PROCREATING_INTERVAL = 2;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single grass.
    private static final int GRASS_FOOD_VALUE = 5;

    // Individual characteristics (instance fields).
    // The rabbit's age.
    private int age;
    // The age of the rabbit when it last bred.
    private int ageLastBred;
    // The rabbit's food level, which is increased by eating grass.
    private int foodLevel;
    // Whether the rabbit is asleep.
    private boolean isAsleep;

    /**
     * Create a new rabbit. A rabbit can be created as a newborn (age zero,
     * with a full stomach, and awake) or with a random age, food level,
     * and it could be awake or asleep.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Rabbit(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        age = 0;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(GRASS_FOOD_VALUE);
            isAsleep = rand.nextBoolean();
        }
        else {
            age = 0;
            foodLevel = GRASS_FOOD_VALUE;
            isAsleep = false;
        }
    }

    /**
     * This is what the rabbit does most of the time - it runs 
     * around and finds food. Sometimes it will procreate or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Organism> newRabbits, boolean isNight)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
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
                }
                if (canProcreate()){
                    procreate(newRabbits);
                }
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
     * Look for grass adjacent to the current location.
     * Only the first grass is eaten.
     * 
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(boolean isNight)
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object organism = field.getObjectAt(where);
            if(organism instanceof Grass) {
                Grass grass = (Grass) organism;
                if(grass.isAlive()) { 
                    grass.setDead();
                    foodLevel = GRASS_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
   
    /**
     * Try to procreate.
     * For all adjacent locations, if any of them is a Rabbit of the opposite sex, try to mate witht them.
     * Newborns are placed in free adjacent locations.
     * 
     * @param newRabbits A list of newly born rabbits.
     */
    public void procreate(List<Organism> newRabbits)
    {
        String sex = getSex();
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> locations = field.adjacentLocations(getLocation());
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit){
                Rabbit rabbit = (Rabbit) animal;
                if (rabbit.getSex() != sex){
                    if (rand.nextDouble() <= PROCREATING_PROBABILITY) {
                        int births = rand.nextInt(MAX_LITTER_SIZE) + 1;
                        for(int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Rabbit young = new Rabbit(false, field, loc);
                            newRabbits.add(young);
                        }
                    }
                }
            }
        }
    }

    /**
     * Increage hunger.
     * If hunger goes below one, rabbit dies.
     */
    private void incrementHunger()
    {
        if (isAsleep) {
            foodLevel -= 0.2;
        } else {
            foodLevel--;
        }
        
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Change the escape probability.
     * 
     * @param newEscapeProbability The new value of escapeProbability.
     */
    public static void setEscapeProbability(double newEscapeProbability)
    {
        escapeProbability = newEscapeProbability;
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
     * A rabbit can procreate if it has reached the procreateing age,
     * or if it hasn't procreated in its procreating interval.
     * 
     * @return true if the rabbit can procreate, false otherwise.
     */
    private boolean canProcreate()
    {
        return (age >= PROCREATING_AGE) && (age >= ageLastBred + PROCREATING_INTERVAL);
    }

    /**
     * Get the escape probability of a rabbit.
     * 
     * @return the escape probability of a rabbit.
     */
    public static double getEscapeProbability(boolean isNight)
    {
        return isNight ? escapeProbability + ESCAPE_PROBABILITY_CHANGE : escapeProbability;
    }
}
