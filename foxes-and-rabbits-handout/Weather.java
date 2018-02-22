
/**
 * Abstract class Weather - A class representing shared characteristics of all weathers.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public abstract class Weather
{
    // Individual characteristics (instance fields).
    // Whether the weather is occuring.
    private boolean occuring;
    
    /**
     * Create an instance of Wether.
     * 
     * @param isNight Whether it is night time.
     */
    public Weather()
    {
        occuring = true;
    }
    
    /**
     * Check if the weather keep going or does it stop.
     */
    public abstract void updateWeather();
    
    /**
     * Change the behaviour of the animals.
     * 
     * @param reset Whether we reset the behaviour of the animals or not.
     */
    public abstract void setProbabilities(boolean reset);
    
    /**
     * Return if the weather is occuring or not.
     * 
     * @return true if it is occuring, false otherwise.
     */
    public boolean isOccuring()
    {
        return occuring;
    }
    
    /**
     * Stop the weather from occuring
     */
    public void endOccurence()
    {
        System.out.println("        ending weather!");
        occuring = false;
    }
}
