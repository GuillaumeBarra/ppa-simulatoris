/**
 * Class for simulating the passage of time in day.
 * Does not account for the date. Only the time of the day.
 * Time is handled in minutes.
 * The day starts at minute 1, which represents 1 minute after midnight.
 * The day ends at minute 1440, which represents midnight.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Time
{
    // Individual characteristics (instance fields).
    // minutes in the day. From 0 to 1440 inclusive.
    private int minutes;
    // minutes in the day when sunrise begins.
    private int sunRise; 
    // minutes in the day when sunset begins.
    private int sunSet;

    /**
     * Create an instance of time.
     * 
     * @param sunRise The time in minutes for the sunrise.
     * @param sunSet The time in minutes for the sunset.
     */
    public Time(int sunRise, int sunSet)
    {
        this.minutes = 480; // time starts at 8AM
        this.sunRise = sunRise;
        this.sunSet = sunSet;
    }
    
    /**
     * Increment minutes variable.
     * Reset to 0 if it surpasses 1440 minutes(24 hours).
     * 
     * @param increment The number of minutes to increment by.
     */
    public void incrementTime(int increment)
    {
        assert (increment > 0) : "increment is less than 1!";
        
        minutes = (minutes + increment) % 1440;
        
    }
    
    /**
     * Return the current time in minutes.
     * @return The current time value in minutes
     */
    public int getTime()
    {
        return minutes;
    }
    
    /**
     * Set the time in minutes to a particular number
     * 
     * @param minutes The value to set minutes to.
     */
    public void setTime(int minutes)
    {
        minutes = minutes;
    }

    /**
     * Return wether or not it is night time.
     * @return true if night, false otherwise
     */
    public boolean isNight()
    {
        return minutes < sunRise || minutes > sunSet;
    }
}