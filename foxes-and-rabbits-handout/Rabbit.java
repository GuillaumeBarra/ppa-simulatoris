import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, procreate, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Rabbit extends Animal
{
    // Characteristics shared by all rabbits (class variables).

    // The probability of a rabbit escaping from a predator.
    private static double escapeProbability = 0.6;
    // The probability change of a rabbit escaping from a predator.
    private static final double ESCAPE_PROBABILITY_CHANGE = 0.1;
    // The age at which a rabbit can start to procreate.
    private static final int PROCREATING_AGE = 2;
    // The age to which a rabbit can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a rabbit procreateing when it meets another rabbit.
    private static final double PROCREATING_PROBABILITY = 0.12;
    // The number of years before a rabbit can procreate again.
    private static final int PROCREATING_INTERVAL = 3;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // The huniting 
    private static final int GRASS_FOOD_VALUE = 10;
    // A shared random number generator to control procreateing.
    private static final Random rand = Randomizer.getRandom();
    // Wether or not the rabbit is asleep.
    private boolean isAsleep = false;
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.5;

    // Individual characteristics (instance fields).

    // The rabbit's age.
    private int age;
    // The age of the rabbit when it last bred.
    private int ageLastBred;
    private int foodLevel;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
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
        }
        else {
            age = 0;
            foodLevel = GRASS_FOOD_VALUE;
        }
    }

    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will procreate or die of old age.
     * @param newRabbits A list to return newly born rabbits.
     */
    public void act(List<Organism> newRabbits, boolean isNight)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if (isAsleep) {return;}
            else {
                if(isNight){
                    isAsleep = rand.nextDouble() <= SLEEP_PROBABILITY ? true : false;
                    if (isAsleep) {return;}

                    escapeProbability += ESCAPE_PROBABILITY_CHANGE; 
                }
                procreate(newRabbits);
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
     * Make this fox more hungry. This could result in the fox's death.
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
     * This could result in the rabbit's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Check whether or not this rabbit is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newRabbits A list to return newly born rabbits.
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
     * A rabbit can procreate if it has reached the procreateing age.
     * @return true if the rabbit can procreate, false otherwise.
     */
    private boolean canProcreate()
    {
        return (age >= PROCREATING_AGE) && (age >= ageLastBred + PROCREATING_INTERVAL);
    }

    /**
     * Get the escape probability of a rabbit.
     * @return the escape probability of a rabbit.
     */
    public double getEscapeProbability(boolean isNight)
    {
        return isNight ? escapeProbability + ESCAPE_PROBABILITY_CHANGE : escapeProbability;
    }
}
