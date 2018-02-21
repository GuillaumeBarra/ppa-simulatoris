
/**
 * Write a description of class Fog here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Fog extends Weather
{
    // instance variables - replace the example below with your own
    private static final int DURATION = 25;// number of steps
    private int age;
    
    // call these constancs ...CHANGE
    private static final double EAGLE_HUNTING_PROBABILITY = -0.05;
    private static final double FOX_HUNTING_PROBABILITY = 0.2;
    private static final double RABBIT_ESCAPE_PROBABILITY = 0.3;
    
    /**
     * Constructor for objects of class Fog
     */
    public Fog(boolean isNight)
    {
        super(isNight);
        age = 0;
        setProbabilities(false);
    }
    
    public void setProbabilities(boolean reset){
        // need to assert these are greater than zero !!
        if (reset) {
                Eagle.setHuntingProbability(Eagle.getHuntingProbability() - EAGLE_HUNTING_PROBABILITY);
                Fox.setHuntingProbability(Fox.getHuntingProbability() - FOX_HUNTING_PROBABILITY);
                Rabbit.setEscapeProbability(Rabbit.getEscapeProbability(isNight) - RABBIT_ESCAPE_PROBABILITY);
        } else {
            Eagle.setHuntingProbability(Eagle.getHuntingProbability() + EAGLE_HUNTING_PROBABILITY);
            Fox.setHuntingProbability(Fox.getHuntingProbability() + FOX_HUNTING_PROBABILITY);
            Rabbit.setEscapeProbability(Rabbit.getEscapeProbability(isNight) + RABBIT_ESCAPE_PROBABILITY);
        }
    }
    
    public void updateWeather(){
        age += 1;
        if (age >= DURATION) {
            setProbabilities(true);
            endOccurence();
        }
    }
}