import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a disease named anthrax.
 * Anthrax tries to infect organisms and once they are infected,
 * there is a chance of the organism dying.
 * 
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
 */
public class Anthrax extends Organism
{
    // A shared random number generator to control procreating.
    private static final Random rand = Randomizer.getRandom();
    // The probability of an organism getting infected
    private static double infectionProbability = 0.5;
    // The probability of an infected organism dying.
    private static final double DEAD_PROBABILITY = 0.3;
    // An ArrayList of type organism used to store the organisms infected.
    protected static ArrayList<Organism> organismsInfected;
    
    /**
     * Create an anthrax. 
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Anthrax(Field field, Location location)
    {
        super(field, location);
        //organismsInfected = new ArrayList<Organism>();
        organismsInfected = new ArrayList<Organism>();
        //(numberOfAnthraxInstances += 1;
        organismsInfected.add(this);
        procreate(organismsInfected);
        organismsInfected.remove(this);
    }
    
    /**
     * Get the number of organisms infected.
     * 
     * @return organismsInfected.size The number of organisms infected.
     */
    public static int getOrganismsInfectedSize(){
        try {
            return organismsInfected.size();
        } catch(NullPointerException ex) {
            return 0;
        }
        }

    /**
     * This is what the anthrax does - it checks if there's any infected organisms 
     * and it tries to kill the infected organisms.
     * 
     * @param newAnthrax A list of type organism.
     * @param isNight Whether it is night time.
     */
    public void act(List<Organism> newAnthrax, boolean isNight){
    if (organismsInfected.size() == 0){
        Simulator.setAnthraxCreated(false);
        setDead();
        return;
    }

    ArrayList<Organism> organismsToRemove = new ArrayList<Organism>();
    for (Organism infected : organismsInfected){
            if (rand.nextDouble() <= DEAD_PROBABILITY && !(infected instanceof Anthrax)){ // the second bolean condition should not need to exist if the logic in the constructor works.
                infected.setDead();
                organismsToRemove.add(infected);
        }
    }
    organismsInfected.removeAll(organismsToRemove);
    procreate(newAnthrax);
    }

    /**
     * This let's the anthrax to infect more organisms
     * 
     * @param newAnthrax A list of type organism
     */
    public void procreate(List<Organism> newAnthrax){
        ArrayList<Organism> organismsToRemove = new ArrayList<Organism>();

        for (Organism infected : organismsInfected){
            if (!(infected.isAlive())){
                organismsToRemove.add(infected);
            }
        }
        organismsInfected.removeAll(organismsToRemove);
        
        ArrayList<Organism> organismsToAdd = new ArrayList<Organism>();
        for (Organism infected : organismsInfected){
            Field field = getField(); // must this be here?
            assert field != null: "field is null";
            List<Location> adjacent = field.adjacentLocations(infected.getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()){
                Location where = it.next();
                Object target = field.getObjectAt(where);
                if (target instanceof Organism){
                    Organism organism = (Organism) target;
                    if (!(organism.isInfected())){
                        if(rand.nextDouble() <= infectionProbability){
                            organismsToAdd.add(organism);
                        }
                    }
                }
            }
        }
        organismsInfected.addAll(organismsToAdd);
    }
    
    /**
     * return the infection probability.
     * 
     * @return Infection probability.
     */
    public static double getInfectionProbability(){
        return infectionProbability;
    }
    
    /**
     * Change the infection probability.
     * 
     * @param newInfectionProbability The new infection probability.
     */
    public static void setInfectionProbability(double newInfectionProbability){
        infectionProbability = newInfectionProbability;
    }

}
