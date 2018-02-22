
/**
 * Simulate rain.
 * Changes the behaviour of some animals.
 *
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Rain extends Weather
{
    // Class variables
    // How long the rain goes on for in steps.
    private static final int DURATION = 30;
    // The change of eagle's hunting probability.
    private static final double EAGLE_HUNTING_PROBABILITY = -0.05;
    // The change of anthrax's infection probability.
    private static final double ANTHRAX_INFECTION_PROBABILITY = 0.2;
    // The change of sloth's escape probability.
    private static final double SLOTH_ESCAPE_PROBABILITY = -0.1;
    
    // Individual characteristics (instance fields).
    // How long the rain has been going on for.
    private int age; 
    // Wether or not it is night
    private boolean isNight;
    /**
     * Start raining and change the behaviour of some animals.
     * 
     * @param isNight Whether it is night time.
     */
    public Rain(boolean isNight)
    {
        super();
        isNight = isNight;
        age = 0;
        setProbabilities(false);
    }
    
    /**
     * Change the behaviour of the animals.
     * 
     * @param reset Whether we reset the behaviour of the organisms or not
     */
    public void setProbabilities(boolean reset)
    {
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
    public void updateWeather()
    {
        age += 1;
        if (age >= DURATION) {
            setProbabilities(true);
            endOccurence();
        }
    }
}
