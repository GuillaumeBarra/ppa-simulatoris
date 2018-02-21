
/**
 * Abstract class Weather - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Weather
{
    // instance variables - replace the example below with your own
    private boolean occuring;
    
    public Weather(){
        occuring = true;
    }
    
    public abstract void updateWeather();
    
    public abstract void setProbabilities(boolean reset);
    
    public boolean isOccuring(){
        return occuring;
    }
    
    public void endOccurence(){
        occuring = false;
    }
}
