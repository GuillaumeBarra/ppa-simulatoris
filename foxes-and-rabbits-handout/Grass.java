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
    // Characteristics shared by all grasses (class variables).
    // A shared random number generator to control procreateing.
    private static final Random rand = Randomizer.getRandom();
    // The age to which a grass can live.
    private static final int MAX_AGE = 20;
    // The likelihood of a iguana procreateing when it meets another iguana.
    private static final double PROCREATING_PROBABILITY = 0.5;
    // The age at which a iguana can start to procreate.
    private static final double PROCREATING_AGE = 5;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    
    // Individual characteristics (instance fields).
    private int age;
    /**
     * Create instance of grass
     * 
     * @param randomAge If true, the grass will have random age and hunger level.
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
     * Try to procreate.
     * New grass is placed in free adjacent locations.
     * 
     * @param newGrasses A list of new grasses.
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
     * Increase grass' age and try to procreate.
     * 
     * @param newGrasses A list to return newly created grass.
     * @param isNight Whether it is night time.
     */
    public void act(List<Organism> newGrasses, boolean isNight){
        incrementAge();
        procreate(newGrasses);
    }
}
