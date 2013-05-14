package japrc2012;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;




public interface ATMSimulationInterface
{
    /**
     * Adds a plane to the simulation.
     * 
     * @param callsign The callsign of the plane to be added
     * @param startAirport The code for the airport the plane took off from
     * @param destinationAirport The code for the airport the plane is flying to
     * @throws SimulationException if either airport does not exist 
     */
    public void addPlane(String callsign, String startAirport, String destinationAirport);
    
    /**
     * Returns a list of the planes in the simulation
     * 
     */
    public ArrayList<PlaneInterface> getPlanes();
    
    /**
     * Returns the object for a specific named plane. 
     * @param callSign the name of the plane
     * @return The named plane
     * @throws SimulationException if the named plane does not exist in the simulation 
     */
    public PlaneInterface getPlane(String callSign);
    
    /**
     * Adds an airport to the simulation.
     * 
     * @param code The code for the airport
     * @param name The full name of the airport 
     * @param location The grid location of the airport    
     */
    public void addAirport(String code, String name, GridLocation location);
    
    /**
     * Returns a list of the airports in the simulation
     *  
     */
    public ArrayList<AirportInterface> getAirports();
    
    /**
     * Returns the object for a specific named airport. 
     * @param code the code for the airport
     * @return The named airport
     * @throws SimulationException if the named airport does not exist in the simulation 
     */
    public AirportInterface getAirport(String code);
    
    /**
     * Tells the simulator that it should notify the specified GUI whenever something changes in the simulated world that may require an update to the GUI. 
     * 
     * @param gui the gui that is to be notified of changes.
     */
    public void setGUI(SimGUIInterface gui);
    
    /**
     * Tells the simulator to move simulated time forward by one tick, and take any actions that should occur in that time. 
     */
    public void tick();
    
    /**
     * Returns the number of ticks since the simulation was started
     */
    public int getSimTime();
        
    /**
     * Gets the dimensions of the simulated world, in grid squares.
     * 
     * @return the dimensions of the world
     */
    public GridLocation getMapDimensions();
    
    /**
     * Sets the dimensions of the simulated world, in grid squares.
     *  
     */
    public void setMapDimensions(int x, int y);
    
    /**
     * Moves the specified plane to the specified location, provided that the specified move is legal. If the move is not legal
     * the method returns with no effect.
     * 
     * @param planeName the name of the plane to move
     * @param newLoc the location to move the plane to
     */
    public void movePlaneTo(String planeName, GridLocation newLoc);
        
    /** 
     * Starts the simulator running continuously, at a rate determined by the most recent call to setTickDelay(), and returns. 
     * If the simulator is already running, it does nothing. 
     */
    public void start();
    
    /** 
     * If the simulator is currently running continuously (after a start()) command, this stops it running. 
     * If the simulator is not running, does nothing. 
     */
    public void pause();
           
    /**
     * Sets the delay between ticks when the simulation is running continuously (after a start() command)
     * 
     * @param millis The real-time delay between ticks, in milliseconds
     */
    public void setTickDelay(int millis);
    
    /** 
     * Tells the simulation to discard the current planes in the simulation, and load a new set from the supplied stream 
     * 
     * @param trafficStream A stream containing data about a new set of planes
     */
    public void loadTraffic(InputStream trafficStream); 

    /** 
     * Tells the simulation to discard the current airports in the simulation, and load a new set from the supplied stream 
     * 
     * @param trafficStream A stream containing data about a new set of airports
     */
    public void loadAirports(InputStream airportStream);
    
    /** 
     * Tells the simulation to log all observable flight events to the supplied stream, ready for later replay
     * 
     * @param replayStream The stream to be logged to
     */
    public void setReplayLogFile(OutputStream replayStream);
    
    /** 
     * Tells the simulation to switch to replay mode, and load a set of flight events from the supplied stream, ready to replay them
     * 
     * @param replayStream The stream to load the events from
     */
    public void loadFlightPathsForReplay(InputStream replayStream);
    
    /** 
     * Tells the simulation to log all airprox events to the supplied stream
     * 
     * @param logStream The stream to be logged to
     */
    public void setLogFile(OutputStream logStream);       
}
