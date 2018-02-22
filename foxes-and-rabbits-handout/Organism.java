import java.util.List;
import java.util.ArrayList;
/**
 * Abstract class Organism - A class representing shared characteristics of all organisms.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public abstract class Organism
{
    // Whether the animal is alive or not.
    private boolean alive;
    // Whether the animal is infected or not.
    private boolean infected;
    // The animal's field.
    private Field field;
    // The animal's position in the field.
    private Location location;
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Organism(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
        infected = false;
    }

    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * 
     * @param newOrganisms A list to receive newly born animals.
     * @param isNight A boolean value that is true when night time, false if otherwise.
     */
    abstract public void act(List<Organism> newOrganisms, boolean isNight);
    
    /**
     * Make this animal procreate - that is: make it breed
     * 
     * @param newOrganisms A list to receive newly born animals.
     */
    abstract public void procreate(List<Organism> newOrganisms); 

    /**
     * Check whether the animal is alive or not.
     * 
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Check whether the animal is alive or not.
     * 
     * @return true if the animal is still alive.
     */
    protected boolean isInfected()
    {
        return infected;
    }
    
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        infected = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * 
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
     * 
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Return the animal's field.
     * 
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
