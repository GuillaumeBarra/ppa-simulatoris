import java.util.List;
import java.util.ArrayList;
/**
 * An abstract class representing shared characteristics of all organisms.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public abstract class Organism
{
        
    // Individual characteristics (instance fields).
    // Whether the organism is alive or not.
    private boolean alive;
    // Whether the organism is infected or not.
    private boolean infected;
    // The organism's field.
    private Field field;
    // The organism's position in the field.
    private Location location;
    /**
     * Create a new organism at location in field.
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
     * Abstract method for main organism behaviour.
     * Implented in children classes.
     * 
     * @param newOrganisms A list to receive newly born organisms.
     * @param isNight A boolean value that is true when night time, false if otherwise.
     */
    abstract public void act(List<Organism> newOrganisms, boolean isNight);
    
    /**
     * Abstract method for organism procreation.
     * Implemented in children classes.
     * 
     * @param newOrganisms A list to receive newly born organisms.
     */
    abstract public void procreate(List<Organism> newOrganisms); 

    /**
     * Check whether the organism is alive or not.
     * 
     * @return true if the organism is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Check whether the organism is infected or not.
     * 
     * @return true if the organism is infected.
     */
    protected boolean isInfected()
    {
        return infected;
    }
    
    /**
     * Kill the organism.
     * Set its location variables to null and remove it from the field.
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
     * Return the organism's location.
     * 
     * @return The organism's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the organism at the new location in the given field.
     * 
     * @param newLocation The organism's new location.
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
     * Return the organism's field.
     * 
     * @return The organism's field.
     */
    protected Field getField()
    {
        return field;
    }
}
