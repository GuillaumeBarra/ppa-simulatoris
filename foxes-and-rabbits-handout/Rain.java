
/**
 * Write a description of class Rain here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Rain extends Weather
{
    // instance variables - replace the example below with your own
    private static final int DURATION = 10;// number of steps
    private int age;
    
    // call these constancs ...CHANGE
    private static final double EAGLE_HUNTING_PROBABILITY = -0.05;
    private static final double ANTHRAX_INFECTION_PROBABILITY = 0.4;
    private static final double SLOTH_ESCAPE_PROBABILITY = -0.1;
    
    /**
     * Constructor for objects of class Rain
     */
    public Rain()
    {
        super();
        age = 0;
        setProbabilities(false);
    }
    
    public void setProbabilities(boolean reset){
        // need to assert these are greater than zero !!
        if (reset) {
            System.out.println("        Trying to reset");
                Eagle.setHuntingProbability(Eagle.getHuntingProbability() - EAGLE_HUNTING_PROBABILITY);
                Anthrax.setInfectionProbability(Anthrax.getInfectionProbability() - ANTHRAX_INFECTION_PROBABILITY);
                Sloth.setEscapeProbability(Sloth.getEscapeProbability() - SLOTH_ESCAPE_PROBABILITY);
            System.out.println("        reset!");
        } else {
            System.out.println("        trying to set");
            Eagle.setHuntingProbability(Eagle.getHuntingProbability() + EAGLE_HUNTING_PROBABILITY);
            Anthrax.setInfectionProbability(Anthrax.getInfectionProbability() + ANTHRAX_INFECTION_PROBABILITY);
            Sloth.setEscapeProbability(Sloth.getEscapeProbability() + SLOTH_ESCAPE_PROBABILITY);
            System.out.println("        set!");
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
