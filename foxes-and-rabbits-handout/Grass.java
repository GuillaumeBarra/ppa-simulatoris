import java.util.Random;
import java.util.List;
import java.util.Iterator;

/**
 * Write a description of class Grass here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Grass extends Plant
{
    private static final int MAX_AGE = 15;
    private static final int FOOD_VALUE = 4;
    private static final double POLLINATION_PROBABILITY = 0.05;
    private static final double POLLINATION_AGE = 3;
    private static final int MAX_LITTER_SIZE = 4; //NOTE beter name
    private static final Random rand = Randomizer.getRandom();
    private int age;

    /**
     * Constructor for objects of class Grass
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

    public void breed(List<Plant> newGrasses)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        if (age >= POLLINATION_AGE) {
            if (rand.nextDouble() <= POLLINATION_PROBABILITY){
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
