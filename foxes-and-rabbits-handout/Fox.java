import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Fox extends Animal
{
    // Characteristics shared by all foxes (class variables).

    // The probability of a fox catching a prey successfully.
    private static double huntingProbability = 0.2;
    // The probability change of a fox catching a prey successfully.
    private static final double HUNTING_PROBABILITY_CHANGE = 0.2;
    // The age at which a fox can start to procreate.
    private static final int PROCREATING_AGE = 15;
    // The age to which a fox can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a fox procreating when it meets another rabbit
    private static final double PROCREATING_PROBABILITY = 0.08;
    // The number of years before a fax can procreate again.
    private static final int PROCREATING_INTERVAL = 9;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a fox can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 9;
    private static final int SLOTH_FOOD_VALUE = 6;
    // A shared random number generator to control procreating.
    private static final Random rand = Randomizer.getRandom();
    // Wether or not the fox is asleep.
    private  boolean isAsleep = false;
    // The probability that the animal falls asleep.
    private static final double SLEEP_PROBABILITY = 0.2;

    private static final double FOX_CREATION_PROBABILITY = 0.08;

    // Individual characteristics (instance fields).
    // The fox's age.
    private int age;
    // The age of the fox when it last bred.
    private int ageLastBred;
    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Fox(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE + SLOTH_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE + SLOTH_FOOD_VALUE; // could be an issue
        }
    }
    
    public static double getHuntingProbability(){
        return huntingProbability;
    }

    public static void setHuntingProbability(double newHuntingProbability){
        assert newHuntingProbability >= 0 : "Eagle hunting probability below zero!!" + newHuntingProbability;
        huntingProbability = newHuntingProbability;
    }

    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might procreate, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newFoxes A list to return newly born foxes.
     */
    public void act(List<Organism> newFoxes, boolean isNight)
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
                }
                procreate(newFoxes); 
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
        return FOX_CREATION_PROBABILITY;
    }

    /**
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
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
                        if (rand.nextDouble() <= rabbit.getEscapeProbability(isNight)) { // NOTE: review this.
                            rabbit.setDead();
                            foodLevel = RABBIT_FOOD_VALUE;
                            return where;
                        }
                    }
                };
            } else if (animal instanceof Sloth){
                Sloth sloth = (Sloth) animal;
                if(sloth.isAlive()) { 
                    if (rand.nextDouble() <= tempHuntingProbability) {
                        if (rand.nextDouble() <= sloth.getEscapeProbability(isNight)) { // NOTE: review this.
                            sloth.setDead();
                            foodLevel = SLOTH_FOOD_VALUE;
                            return where;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
    public void procreate(List<Organism> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        String sex = getSex();
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        List<Location> locations = field.adjacentLocations(getLocation());
        Iterator<Location> it = locations.iterator();
        while(it.hasNext()){
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fox){
                Fox fox = (Fox) animal;
                if (fox.getSex() != sex){
                    if (rand.nextDouble() <= PROCREATING_PROBABILITY) {
                        int births = rand.nextInt(MAX_LITTER_SIZE) + 1;
                        for(int b = 0; b < births && free.size() > 0; b++) {
                            Location loc = free.remove(0);
                            Fox young = new Fox(false, field, loc);
                            newFoxes.add(young);
                        }
                    }
                }
            }
        }
    }

    
    // THIS METHOD CAN BE DELETED   
    /**
     * Generate a number representing the number of births,
     * if it can procreate.
     * @return The number of births (may be zero).
     */
    private int procreate()
    {
        int births = 0;
        if(canProcreate() && rand.nextDouble() <= PROCREATING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A fox can procreate if it has reached the procreating age.
     */
    private boolean canProcreate()
    {
        return (age >= PROCREATING_AGE) && (age >= ageLastBred + PROCREATING_INTERVAL);
    }
}
