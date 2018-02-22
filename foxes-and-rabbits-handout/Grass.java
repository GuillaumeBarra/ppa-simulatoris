import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a grass.
 * Grass just procreates.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Grass extends Plant
{
    private static final int MAX_AGE = 20;
    private static final double PROCREATING_PROBABILITY = 0.5;
    private static final double PROCREATING_AGE = 5;
    private static final int MAX_LITTER_SIZE = 2; //NOTE better name
    private static final Random rand = Randomizer.getRandom();
    private int age;

    /**
     * Create instancs of grass
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Grass(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
        else {
            age = 0;
        }
    }

    /**
     * Check whether or not this grass is to procreate at this step.
     * New births will be made into free adjacent locations.
     * 
     * @param newGrasses A list to return newly created grass.
     */
    public void procreate(List<Organism> newGrasses)
    {
        if (isAlive()){
            Field field = getField();
            List<Location> free = field.getFreeAdjacentLocations(getLocation());
            if (age >= PROCREATING_AGE) {
                if (rand.nextDouble() <= PROCREATING_PROBABILITY){
                    int births = rand.nextInt(MAX_LITTER_SIZE) + 1;
                    for(int b = 0; b < births && free.size() > 0; b++) {
                        Location loc = free.remove(0);
                        Grass young = new Grass(false, field, loc);
                        newGrasses.add(young);
                    }
                }
            }
        }
    }

    /**
     * Increase the age. This could result in the grass's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    /**
     * Let the grass increase in age and create new grasses.
     * 
     * @param newGrasses A list to return newly created grass.
     * @param isNight Whether it is night time.
     */
    public void act(List<Organism> newGrasses, boolean isNight){
        incrementAge();
        procreate(newGrasses);
    }
}
