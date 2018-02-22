import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a sloth.
 * Sloths sleep, eat, age, move, procreate, and die.
 * 
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Sloth extends Animal
{
    // Characteristics shared by all sloths (class variables).
    // A shared random number generator to control procreateing.
    private static final Random rand = Randomizer.getRandom();
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.5;
    // The probability of a sloth escaping from a predator.
    private static double escapeProbability = 0.2;
    // The probability change of a sloth escaping from a predator.
    private static final double ESCAPE_PROBABILITY_CHANGE = 0.1;
    // The age to which a sloth can live.
    private static final int MAX_AGE = 35;
    // The age at which a sloth can stat to procreate.
    private static final int PROCREATING_AGE = 5;
    // The likelihood of a sloth procreateing when it meets another sloth.
    private static final double PROCREATING_PROBABILITY = 0.3;
    // The number of years before a sloth can procreate again.
    private static final int PROCREATING_INTERVAL = 3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The food value of a single grass.
    private static final int GRASS_FOOD_VALUE = 6;

    // Individual characteristics (instance fields).
    // The sloth's age.
    private int age;
    // The age of the sloth when it last bred.
    private int ageLastBred;
    // The sloth's food level, which is increased by eating grass.
    private int foodLevel;
    // Whether the sloth is asleep.
    private boolean isAsleep;

    /**
     * Create a new sloth. A sloth can be created as a newborn (age zero,
     * with a full stomach, and awake) or with a random age, food level,
     * and it could be awake or asleep.
     * 
     * @param randomAge If true, the sloth will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Sloth(boolean randomAge, Field field, Location location)
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
     * This is what the sloth does most of the time - it runs 
     * around and finds food. 
     * Sometimes it will procreate or die of old age.
     * 
     * @param newSloths A list to return newly born sloths.
     * @param isNight Whether it is night time.
     */
    public void act(List<Organism> newSloths, boolean isNight)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
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
                    procreate(newSloths);
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
     * Look for sloths adjacent to the current location.
     * Only the first live sloth is eaten.
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
     * For all adjacent locations, if any of them is an Sloth of the opposite sex, try to mate witht them.
     * Newborns are placed in free adjacent locations.
     * 
     * @param newSloth A list of newly born sloths.
     */
    public void procreate(List<Organism> newSloths)
    {
        String sex = getSex();
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> locations = field.adjacentLocations(getLocation());
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Sloth){
                Sloth sloth = (Sloth) animal;
                if (sloth.getSex() != sex){
                    if (rand.nextDouble() <= PROCREATING_PROBABILITY) {
                        int births = rand.nextInt(MAX_LITTER_SIZE) + 1;
                        for(int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Sloth young = new Sloth(false, field, loc);
                            newSloths.add(young);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Return the escape probability.
     * 
     * @return escape probability.
     */
    public static double getEscapeProbability()
    {
        return escapeProbability;
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
     * Increage hunger.
     * If hunger goes below one, sloth dies.
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
     * A sloth can procreate if it has reached the procreateing age,
     * or if it hasn't procreated in its procreating interval.
     * 
     * @return true if the sloth can procreate, false otherwise.
     */
    private boolean canProcreate()
    {
        return (age >= PROCREATING_AGE) && (age >= ageLastBred + PROCREATING_INTERVAL);
    }

    /**
     * Get the escape probability of a sloth.
     * 
     * @return the escape probability of a sloth.
     */
    public static double getEscapeProbability(boolean isNight)
    {
        return isNight ? escapeProbability + ESCAPE_PROBABILITY_CHANGE : escapeProbability;
    }
}