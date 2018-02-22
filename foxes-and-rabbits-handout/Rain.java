
/**
 * Make it rain.
 * Changes the behaviour of some animals.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Rain extends Weather
{
    // How long the rain goes on for.
    private static final int DURATION = 10;// number of steps
    // How long the rain has been going on for.
    private int age; 
    
    // The change of eagle's hunting probability.
    private static final double EAGLE_HUNTING_PROBABILITY = -0.05;
    // The change of anthrax's infection probability.
    private static final double ANTHRAX_INFECTION_PROBABILITY = 0.4;
    // The change of sloth's escape probability.
    private static final double SLOTH_ESCAPE_PROBABILITY = -0.1;
    
    /**
     * Start raining and change the behaviour of some animals.
     * 
     * @param isNight Whether it is night time.
     */
    public Rain(boolean isNight)
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
            Anthrax.setInfectionProbability(Anthrax.getInfectionProbability() - ANTHRAX_INFECTION_PROBABILITY);
            Sloth.setEscapeProbability(Sloth.getEscapeProbability() - SLOTH_ESCAPE_PROBABILITY);
        } else {
            Eagle.setHuntingProbability(Eagle.getHuntingProbability() + EAGLE_HUNTING_PROBABILITY);
            Anthrax.setInfectionProbability(Anthrax.getInfectionProbability() + ANTHRAX_INFECTION_PROBABILITY);
            Sloth.setEscapeProbability(Sloth.getEscapeProbability() + SLOTH_ESCAPE_PROBABILITY);
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
