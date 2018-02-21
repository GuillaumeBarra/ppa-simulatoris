import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Write a description of class Anthrax here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Anthrax extends Organism
{
    // A shared random number generator to control procreating.
    private static final Random rand = Randomizer.getRandom();

    private static final double INFECTION_PROBABILITY = 0.9;
    private static final double DEAD_PROBABILITY = 0.9;
    private static final int PROCREATION_THRESHOLD = 10;

    protected static ArrayList<Organism> organismsInfected;
    //private static int numberOfAnthraxInstances = 0;
    
    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
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
        System.out.println(organismsInfected.size());
        procreate(organismsInfected);
        System.out.println(organismsInfected.size());
        organismsInfected.remove(this);
        System.out.println(organismsInfected.size());
        for (Organism x : organismsInfected){
            System.out.println(x);
        }

    }

    public void act(List<Organism> newAnthrax, boolean isNight){
        // explain this
        // int realSizeOfOrganismsInfected = organismsInfected.size() - numberOfAnthraxInstances;
        // System.out.println(realSizeOfOrganismsInfected);
        // System.out.println(organismsInfected.size());
        // System.out.println(numberOfAnthraxInstances);
        if (organismsInfected.size() == 0){
            System.out.println("        Set dead anthrax");
            System.out.println(this);
            setDead();
        }

        ArrayList<Organism> organismsToRemove = new ArrayList<Organism>();
        for (Organism infected : organismsInfected){
                if (rand.nextDouble() <= DEAD_PROBABILITY && !(infected instanceof Anthrax)){ // the second bolean condition should not need to exist if the logic in the constructor works.
                    System.out.println("        infected dead");
                    System.out.println(infected);
                    infected.setDead();
                    organismsToRemove.add(infected);
            }
        }
        organismsInfected.removeAll(organismsToRemove);
        procreate(newAnthrax);
        System.out.println("        infected size:");
        System.out.println(organismsInfected.size());
    }

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
                        if(rand.nextDouble() <= INFECTION_PROBABILITY){
                            organismsToAdd.add(organism);
                            // System.out.println("        infectedOrganism:");
                        }
                    }
                }
            }
        }
        organismsInfected.addAll(organismsToAdd);
        // if (organismsInfected.size() >= 10000000){
            // Organism lastInfected = organismsInfected.get(organismsInfected.size() - 1);
            // Location location = lastInfected.getLocation();
            // Anthrax young = new Anthrax(field, location);
            // newAnthrax.add(young);
        // }
    }

}
