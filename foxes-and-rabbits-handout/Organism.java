import java.util.List;

/**
 * Abstract class Organism - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Organism
{
    // Whether the animal is alive or not.
    private boolean alive;
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
    }

    /**
     * Make this animal act - that is: maL
     * ke it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @param isNight A boolean value that is true when night time, false if otherwise.
     */
    abstract public void act(List<Organism> newOrganisms, boolean isNight);

    abstract public void breed(List<Organism> newOrganism); 

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    protected boolean isAlive()
    {
        //        System.out.println("        Alive called!");
        return alive;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        //  System.out.println("Dead!");
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Place the animal at the new location in the given field.
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
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
}
