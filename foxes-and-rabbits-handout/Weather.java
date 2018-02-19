
/**
 * Abstract class Weather - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Weather
{
    // instance variables - replace the example below with your own
    private int x;
    
    public Weather(){
        updateOrganismsProbabilities();
    }
    
    abstract public void updateOrganismsProbabilities();

    /**
     * An example of a method - replace this comment with your own
     *
     * @param  y  a sample parameter for a method
     * @return    the sum of x and y
     */
    public int sampleMethod(int y)
    {
        // put your code here
        return x + y;
    }
    
    abstract public Weather updateWeather();
    
    abstract public void resetOrganismProbabilities();
}
