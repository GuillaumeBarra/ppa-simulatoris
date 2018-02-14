/**
 * Class for simulating the passage of time in day.
 * Does not account for the date. Only the time of the day.
 * Time is handled in minutes.
 *
 * @author Sebastian Tranaeus and Peter Xu
 * @version 1.0
 */
public class Time
{
    // minutes in the day. From 0 to 1440 inclusive.
    private int minutes;
    // minutes in the day when sunrise begins.
    private int sunRise; 
    // minutes in the day when sunset begins.
    private int sunSet;

    /**
     * Constructor for objects of class Time
     */
    public Time(int sunRise, int sunSet)
    {
        this.minutes = 0;
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
     * Return wether or not it is night time.
     * @return true if night, false otherwise
     */
    public boolean isNight()
    {
        return minutes < sunRise || minutes > sunSet;
    }
}