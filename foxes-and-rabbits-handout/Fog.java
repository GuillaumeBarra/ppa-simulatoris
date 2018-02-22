
/**
 * Make it foggy.
 * Changes the behaviour of some animals.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Fog extends Weather
{
    // How long the fog will go on for.
    private static final int DURATION = 25;// number of steps
    // How long the fog has been going on.
    private int age;
    
    // The change of eagle's hunting probability.
    private static final double EAGLE_HUNTING_PROBABILITY = -0.05;
    // The change of fox's hunting probability.
    private static final double FOX_HUNTING_PROBABILITY = 0.2;
    // The change of rabbit's escape probability.
    private static final double RABBIT_ESCAPE_PROBABILITY = 0.3;
    
    /**
     * Make it foggy and change the behaviour of some animals.
     * 
     * @param isNight Whether it is night time.
     */
    public Fog(boolean isNight)
    {
        super(isNight);
        age = 0;
        setProbabilities(false);
    }
    
    /**
     * Change the behaviour of the animals.
     * 
     * @param reset Whether we reset the behaviour of the animals
     */
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
    
    /**
     * Check if the weather keep going or does it stop.
     */
    public void updateWeather(){
        age += 1;
        if (age >= DURATION) {
            setProbabilities(true);
            endOccurence();
        }
    }
}