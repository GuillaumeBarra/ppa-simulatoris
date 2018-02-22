import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 
 * @author Sebastian Tranaeus and Fengnachuan Xu
 * @version 22/02/2018
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
    // The probability that an iguana will be created in any given grid position.
    private static final double IGUANA_CREATION_PROBABILITY = 0.13;
    // The probability that a sloth will be created in any given grid position.
    private static final double SLOTH_CREATION_PROBABILITY = 0.12;
    // The probability that an anthrax will be created in any given grid postition.
    private static final double ANTHRAX_CREATION_PROBABILITY = 0.005;
    // The probability that there's rains.
    private static final double RAIN_PROBABILITY = 0.2;
    // The probability that there's fog.
    private static final double FOG_PROBABILITY = 0.1;
    // Whether it is raining.
    private static boolean isRaining = false;
    // Whether it is foggy.
    private static boolean isFoggy = false;
    // List of organisms in the field.
    private List<Organism> organisms;
    // List of weather.
    private List<Weather> occuringWeather;
    // List of organism classes.
    private List<String> organismClasses;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // Time of the day.
    private Time time;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Whether anthrax was created.
    private static boolean anthraxCreated;
    // A shared random number generator to control weather and the instance of organisms when we populate.
    private Random rand = Randomizer.getRandom();

    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, DEFAULT_SUNRISE_TIME, DEFAULT_SUNSET_TIME);
    }

    /**
     * Create a simulation field with the given size.
     * 
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     * @param sunRise The time of day it changes from night to day.
     * @param sunSet The time of day it changes from day to night.
     */
    public Simulator(int depth, int width, int sunRise, int sunSet)
    {
        // Check if the number they gave was greater than zero.
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        organisms = new ArrayList<>();
        occuringWeather = new ArrayList<>();
        field = new Field(depth, width);
        time = new Time(sunRise, sunSet);

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
     * Set the value of anthraxCreated to newAnthraxCreated.
     *  
     * @param newAnthraxCreated A new value for anthraxCreated.
     */
    public static void setAnthraxCreated(boolean newAnthraxCreated){
        anthraxCreated = newAnthraxCreated;
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
     * 
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
     * organisms.
     */
    public void simulateOneStep()
    {
        step++;
        time.incrementTime(TIME_PER_STEP);
        boolean isNight = time.isNight();
        updateWeather(isNight);

        // Provide space for newborn animals.
        List<Organism> newOrganisms = new ArrayList<>();        
        // Let all organisms act.
        for(Iterator<Organism> it = organisms.iterator(); it.hasNext(); ) {
            Organism organism = it.next();
            organism.act(newOrganisms, isNight);
            if(! organism.isAlive()) {
                it.remove();
            }
            }
        // Add the newly born foxes and rabbits to the main lists.
        organisms.addAll(newOrganisms);
        view.showStatus(step, field, isNight);
        }

    /**
     * For every weather instances, it tries to update them.
     * If a weather doesnt exist, it tries to create it using the createWeather method.
     * 
     * @param isNight Whether it is night time.
     */
    private void updateWeather(boolean isNight){
        createWeather(isNight);
        List<Weather> weatherToRemove = new ArrayList<Weather>();
        for (Weather weatherInstance : occuringWeather){
            weatherInstance.updateWeather();
            if (! weatherInstance.isOccuring()){
                weatherToRemove.add(weatherInstance);
            }
        }
        occuringWeather.removeAll(weatherToRemove);
    }

    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        organisms.clear();
        populate();
        createWeather(time.isNight());
        time.setTime(480);
        boolean isNight = time.isNight();

        // Show the starting state in the view.
        view.showStatus(step, field, isNight);
    }

    /**
     * Randomly make the day rain, fog or sunny.
     * 
     * @param isNight Whether it is night time.
     */
    private void createWeather(boolean isNight){
        if (rand.nextDouble() <= RAIN_PROBABILITY && !isRaining){
            Rain rain = new Rain(isNight);
            isRaining = true;
            occuringWeather.add(rain);
        } else if(rand.nextDouble() <= FOG_PROBABILITY && !isFoggy){
            Fog fog = new Fog(isNight);
            isFoggy = true;
            occuringWeather.add(fog);
        }
        // else, there are no particular weather phenonma occuring. Implicit good weather.
    }

    /**
     * Randomly populate the field with foxes, rabbits, eagles, iguanas, sloths, grass and anthrax.
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
                }
                else if(rand.nextDouble() <= ANTHRAX_CREATION_PROBABILITY && !anthraxCreated) {
                    anthraxCreated = true;
                    Location location = new Location(row, col);
                    Anthrax anthrax = new Anthrax(field, location);
                    organisms.add(anthrax);
                }
                // else leave the location empty.
            }
        }
    }

    /**
     * Pause for a given time.
     * 
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
