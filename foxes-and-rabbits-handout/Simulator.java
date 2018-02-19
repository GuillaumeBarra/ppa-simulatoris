import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The default time in minutes for the sunrise.
    private static final int DEFAULT_SUNRISE_TIME = 0;
    // The default time in minutes for the sunset.
    private static final int DEFAULT_SUNSET_TIME = 1080;
    // The number of minutes per step.
    private static final int TIME_PER_STEP = 60;
    // The probability that a fox will be created in any given grid position.
    private static final double FOX_CREATION_PROBABILITY = 0.08;
    // The probability that a rabbit will be created in any given grid position.
    private static final double RABBIT_CREATION_PROBABILITY = 0.15; 
    // The probability that grass will be created in any given grid position.
    private static final double GRASS_CREATION_PROBABILITY = 0.5;
    // The probability that an eagle will be created in any given grid position.
    private static final double EAGLE_CREATION_PROBABILITY = 0.11;
    // The probability that an eagle will be created in any given grid position.
    private static final double IGUANA_CREATION_PROBABILITY = 0.13;
    // The probability that an eagle will be created in any given grid position.
    private static final double SLOTH_CREATION_PROBABILITY = 0.12;
    
    // The probability that it will rain.
    private static final double RAIN_PROBABILITY = 0.1;
    
    // List of animals in the field.
    private List<Organism> organisms;
    // List of organism classes.
    private List<String> organismClasses;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // An instance of time: TODO: better comment
    private Time time;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    private Random rand = Randomizer.getRandom();
    
    private Weather currentWeather;

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, DEFAULT_SUNRISE_TIME, DEFAULT_SUNSET_TIME);
    }

    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width, int sunRise, int sunSet)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }

        organisms = new ArrayList<>();
        field = new Field(depth, width);

        time = new Time(sunRise, sunSet);
        
        currentWeather = null;

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        view.setColor(Rabbit.class, Color.ORANGE);
        view.setColor(Fox.class, Color.BLUE);
        view.setColor(Grass.class, Color.GREEN);
        view.setColor(Eagle.class, Color.RED);
        view.setColor(Iguana.class, Color.BLACK);
        view.setColor(Sloth.class, Color.PINK);
        // Setup a valid starting point.
        reset();
    }

    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(4000);
    }

    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(60);   // uncomment this to run more slowly
        }
    }

    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        time.incrementTime(TIME_PER_STEP);
        boolean isNight = time.isNight();
        
        if (currentWeather == null){
            currentWeather = createWeather();
        } else {
            currentWeather = updateWeather();
        }
        
        // Upd
        Weather weather = updateWeather();
        if (weather != null){
            weather.updateOrganismsProbabilities();
        }
        
        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms, isNight);
            if(! organism.isAlive()) {
                it.remove();
            }
        }

        // Add the newly born foxes and rabbits to the main lists.
        organisms.addAll(newOrganisms);

        view.showStatus(step, field);
    }
    
    public Weather createWeather(){
        if (rand.nextDouble() <= RAIN_PROBABILITY) {
            Rain rain = new Rain();
            return rain;
        }
        return null;
    }
    
    public Weather updateWeather(){
        if (currentWeather instanceof Rain){
            Rain rain = (Rain) currentWeather;
            return rain.updateWeather();
        }
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        time.setTime(500);

        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= FOX_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Fox fox = new Fox(true, field, location);
                    organisms.add(fox);
                }
                else if(rand.nextDouble() <= RABBIT_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Rabbit rabbit = new Rabbit(true, field, location);
                    organisms.add(rabbit);
                }
                else if(rand.nextDouble() <= EAGLE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Eagle eagle = new Eagle(true, field, location);
                    organisms.add(eagle);
                }
                else if(rand.nextDouble() <= IGUANA_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Iguana iguana = new Iguana(true, field, location);
                    organisms.add(iguana);
                }
                else if(rand.nextDouble() <= SLOTH_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Sloth sloth = new Sloth(true, field, location);
                    organisms.add(sloth);
                }
                else if(rand.nextDouble() <= GRASS_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Grass grass = new Grass(true, field, location);
                    organisms.add(grass);
                    // else leave the location empty.
                }
                
            }
        }
    }

    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
