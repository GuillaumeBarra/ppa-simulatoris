import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of plants.
 * 
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public abstract class Plant extends Organism
{
    // The plant's age.
    private int age;
    
    /**
     * Create a new plant at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Plant(Field field, Location location)
    {
        super(field, location);
    }

}
