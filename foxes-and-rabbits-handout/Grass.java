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
    private static final int MAX_AGE = 20;
    //private static final int FOOD_VALUE = 4;
    private static final double PROCREATING_PROBABILITY = 0.5;
    private static final double PROCREATING_AGE = 5;
    private static final int MAX_LITTER_SIZE = 2; //NOTE better name
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
     * Increase the age. This could result in the fox's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }

    public void act(List<Organism> newGrasses, boolean isNight){
        incrementAge();
        procreate(newGrasses);
    }
}
