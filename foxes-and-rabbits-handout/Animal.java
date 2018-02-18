import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism
{
    // The animal's sex.
    private String sex;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
        super(field, location);
        Random rand = new Random();
        int sex_probability = rand.nextInt(2);
        
        if (sex_probability == 0){
            sex = "male";
        } else {
            sex = "female";
        }
        
    }
    
    /**
     * Return the animal's sex.
     * @return the animal's sex.
     */
    protected String getSex()
    {
        return sex;
    }

}
