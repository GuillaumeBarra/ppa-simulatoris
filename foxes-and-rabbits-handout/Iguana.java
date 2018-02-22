import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a iguana.
 * Iguanas sleep, eat, age, move, procreate, and die.
 * 
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Iguana extends Animal
{
    // Characteristics shared by all iguanas (class variables).

    // The probability of a iguana escaping from a predator.
    private static double escapeProbability = 0.3;
    // The probability change of a iguana escaping from a predator.
    private static final double ESCAPE_PROBABILITY_CHANGE = 0.1;
    // The age at which a iguana can start to procreate.
    private static final int PROCREATING_AGE = 10;
    // The age to which a iguana can live.
    private static final int MAX_AGE = 25;
    // The likelihood of a iguana procreateing when it meets another iguana.
    private static final double PROCREATING_PROBABILITY = 0.22;
    // The number of years before a iguana can procreate again.
    private static final int PROCREATING_INTERVAL = 8;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single grass. In effect, this is the
    // number of steps a iguana can go before it has to eat again. 
    private static final int GRASS_FOOD_VALUE = 4;
    // A shared random number generator to control procreateing.
    private static final Random rand = Randomizer.getRandom();
    // Wether or not the iguana is asleep.
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.5;

    // Individual characteristics (instance fields).

    // The iguana's age.
    private int age;
    // The age of the iguana when it last bred.
    private int ageLastBred;
    // The sloth's food level, which is increased by eating grass.
    private int foodLevel;
    // Whether the sloth is asleep.
    private boolean isAsleep;

    /**
     * Create a new iguana. A iguana may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the iguana will have a random age.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Iguana(boolean randomAge, Field field, Location location)
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
     * This is what the iguana does most of the time - it runs 
     * around and find food. Sometimes it will procreate or die of old age.
     * 
     * @param newIguanas A list to return newly born iguanas.
     */
    public void act(List<Organism> newIguanas, boolean isNight)
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
                procreate(newIguanas);
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
     * Make this iguana more hungry. This could result in the iguana's death.
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
     * Look for iguanas adjacent to the current location.
     * Only the first live iguana is eaten.
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
     * Increase the age.
     * This could result in the iguana's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this iguana is to give birth at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newIguanas A list to return newly born iguanas.
     */
    public void procreate(List<Organism> newIguanas)
    {
        String sex = getSex();
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> locations = field.adjacentLocations(getLocation());
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Iguana){
                Iguana iguana = (Iguana) animal;
                if (iguana.getSex() != sex){
                    if (rand.nextDouble() <= PROCREATING_PROBABILITY) {
                        int births = rand.nextInt(MAX_LITTER_SIZE) + 1;
                        for(int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Iguana young = new Iguana(false, field, loc);
                            newIguanas.add(young);
                        }
                    }
                }
            }
        }
    }

    /**
     * An iguana can procreate if it has reached the procreateing age.
     * 
     * @return true if the iguana can procreate, false otherwise.
     */
    private boolean canProcreate()
    {
        return (age >= PROCREATING_AGE) && (age >= ageLastBred + PROCREATING_INTERVAL);
    }

    /**
     * Get the escape probability of an iguana.
     * 
     * @return the escape probability of a iguana.
     */
    public static double getEscapeProbability(boolean isNight)
    {
        return isNight ? escapeProbability + ESCAPE_PROBABILITY_CHANGE : escapeProbability;
    }
}
