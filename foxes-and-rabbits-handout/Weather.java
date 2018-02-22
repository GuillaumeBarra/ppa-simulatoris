
/**
 * Abstract class Weather - A class representing shared characteristics of all weathers.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public abstract class Weather
{
    // Whether the weather is occuring.
    private boolean occuring;
    // Whether it is night time.
    protected boolean isNight;
    
    /**
     * 
     */
    public Weather(boolean isNight){
        occuring = true;
        isNight = isNight;
    }
    
    /**
     * Check if the weather keep going or does it stop.
     */
    public abstract void updateWeather();
    
    /**
     * Change the behaviour of the animals.
     * 
     * @param reset Whether we reset the behaviour of the animals
     */
    public abstract void setProbabilities(boolean reset);
    
    /**
     * Return the whether if the weather is occuring.
     * 
     * @return true if it is occuring.
     */
    public boolean isOccuring(){
        return occuring;
    }
    
    /**
     * Stop the weather from occuring
     */
    public void endOccurence(){
        occuring = false;
    }
}
