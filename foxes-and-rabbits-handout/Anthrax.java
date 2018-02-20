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
    private static final double DEAD_PROBABILITY = 0.12;
    private static final int PROCREATION_THRESHOLD = 20;

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
        System.out.println("    location.");
        System.out.println(location);
        organismsInfected.add(this);
        procreate(organismsInfected);
    }

    public void act(List<Organism> newAnthrax, boolean isNight){
        if (organismsInfected.size() == 0){
            setDead();
        }

        ArrayList<Organism> organismsToRemove = new ArrayList<Organism>();
        for (Organism infected : organismsInfected){
            if (!(infected instanceof Anthrax)){
                if (rand.nextDouble() <= DEAD_PROBABILITY){
                    System.out.println(infected);
                    infected.setDead();
                    organismsToRemove.add(infected);
                }
            }
        }
        organismsInfected.removeAll(organismsToRemove);
        procreate(newAnthrax);
        System.out.println("        infected size:");
        System.out.println(organismsInfected.size());
    }

    public void procreate(List<Organism> newAnthrax){
        Field field = getField();
        ArrayList<Organism> organismsToAdd = new ArrayList<Organism>();
        for (Organism infected : organismsInfected){
            // System.out.println("        infected::");
            // System.out.println(infected.isAlive()); 
            // System.out.println(infected);
            List<Location> adjacent = field.adjacentLocations(infected.getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()){
                Location where = it.next();
                Object target = field.getObjectAt(where);
                // System.out.println("        Target:");
                // System.out.println(target);
                if (target instanceof Organism){
                    Organism organism = (Organism) target;
                    if (!(organism.isInfected())){
                        if(rand.nextDouble() <= INFECTION_PROBABILITY){
                            organismsToAdd.add(organism);
                            // System.out.println("        infectedOrganism:");
                            System.out.println(organism);
                        }
                    }
                }
            }
        }
        organismsInfected.addAll(organismsToAdd);
        if (organismsInfected.size() >= PROCREATION_THRESHOLD){
            Organism lastInfected = organismsInfected.get(organismsInfected.size() - 1);
            Location location = lastInfected.getLocation();
            Anthrax young = new Anthrax(field, location);
            newAnthrax.add(young);
        }
    }
    
}
