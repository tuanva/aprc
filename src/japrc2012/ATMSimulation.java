/**
 *
 * Exam number: Y0239881
 *
 */


package japrc2012;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public final class ATMSimulation implements ATMSimulationInterface {
    private Map<String, Plane> planes = new TreeMap<String, Plane>();
    private Map<String, Airport> airports = new TreeMap<String, Airport>();

    private SimGUIInterface gui;

    private BufferedReader reader;
    private Utils util;
    private int simTime = 0;
    private Timer timer = null;
    private static int TIMER_DELAY = 1000;
    private OutputStream os;
    private GridLocation mapDimension;

    private Map<String, String> incidents;
    private StringBuilder incidentsBuffer;

    public ATMSimulation() {
        util = new Utils();

        // initial default map dimension
        mapDimension = new GridLocation(500, 382);
        incidentsBuffer = new StringBuilder("");
    }

    @Override
    public void addPlane(String callsign, String startAirport, String destinationAirport) {
        if (planes.containsKey(callsign))
            throw new SimulationException(String.format("Plane %s is already existed.", callsign));

        if (!airports.containsKey(startAirport))
            throw new SimulationException(String.format("Invalid start airport: %s", startAirport));

        if (!airports.containsKey(destinationAirport))
            throw new SimulationException(String.format("Invalid destination airport: %s", destinationAirport));

        Plane p = new Plane(callsign);
        AirportInterface source = airports.get(startAirport);
        p.setLocation(source.getLocation());
        p.setDestination(airports.get(destinationAirport));
        p.setSource(source);
        planes.put(callsign, p);
    }

    @Override
    public ArrayList<PlaneInterface> getPlanes() {
        ArrayList<PlaneInterface> planeList = new ArrayList<PlaneInterface>();
        for (Plane p : planes.values()) {
            planeList.add(p);
        }
        return planeList;
    }

    @Override
    public PlaneInterface getPlane(String callSign) {
        return planes.get(callSign);
    }

    @Override
    public void addAirport(String code, String name, GridLocation location) {
        airports.put(code, new Airport(code, name, location));
    }

    @Override
    public ArrayList<AirportInterface> getAirports() {
        ArrayList<AirportInterface> airportList = new ArrayList<AirportInterface>();
        for (Airport a : airports.values()) {
            airportList.add(a);
        }

        return airportList;
    }

    @Override
    public AirportInterface getAirport(String callSign) {
        return airports.get(callSign);
    }

    @Override
    public void setGUI(SimGUIInterface gui) {
        this.gui = gui;
    }

    @Override
    public void tick() {
//        System.out.println("********* Tick (" + simTime + ") begin *********");

        incidentsBuffer = new StringBuilder("");
        Iterator<Map.Entry<String, Plane>> it = planes.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Plane> entry = it.next();

            Plane p = entry.getValue();
            // check whether the plane arrives the destination if yes: land the plane
            if (p.getLocation().equals(p.getDestination().getLocation())) {
                // System.out.println("Plane: " + p.getName() + " has landed");
                it.remove();

                // jump out of the loop and continue
                continue;
            }

            p.tick();

            // detect incidents
            this.detectIncidents(p);
        }

        for (Airport a : airports.values()) {
            // take of randomly from the current airport
            // check if the plane can take of from the current airport
            if (airports.size() > 1 && !util.isNightTime(simTime)) {
                if (a.canTakeOff()) {
                    // increase counter
                    a.tick();

                    // add new plane to the simulation
                    this.addPlane(a.getCode() + "_" + a.getPlaneCounter(), a.getCode(), randomAirport(a).getCode());

//                    System.out.println(a.getCode() + "_" + a.getPlaneCounter() + " has taken off from " + a.getCode());
//                    System.out.println();
                }
            }
        }

        // update simTime
        simTime = (simTime == Utils.CommonVariables.ONE_DAY_TICKS) ? 1 : simTime + 1;

        updateGUI();

//        System.out.println("********* Tick (" + (simTime - 1) + ") end   *********");
//        System.out.println();
    }

    /**
     * Detect and log unexpected airport incidents when two planes come
     * dangerously close to each other
     */
    private void detectIncidents(Plane planeToCompare) {
        incidents = new TreeMap<String, String>();

        for (PlaneInterface p : getPlanes()) {
            if ((!p.equals(planeToCompare)) && p.getLocation().equals(planeToCompare.getLocation())) {
                if (!incidents.containsKey(p.getName()) && !incidents.containsValue(p.getName())) {
                    incidents.put(p.getName(), planeToCompare.getName());

                    System.out.println(String.format(Utils.CommonVariables.AIRPROX_LOG_FORMAT, p.getName(), planeToCompare.getName(), p.getLocation().getX(), p.getLocation().getY(), getSimTime()));

                    incidentsBuffer.append(String.format(Utils.CommonVariables.AIRPROX_LOG_FORMAT, p.getName(), planeToCompare.getName(), p.getLocation().getX(), p.getLocation().getY(), getSimTime()));
                    incidentsBuffer.append(System.getProperty("line.separator"));
                }
            }
        }
    }

    /**
     * Get a random airport which is different with the @currentAirport from airport list
     *
     * @param currentAirport
     * @return A random airport from airport list
     */

    public AirportInterface randomAirport(Airport currentAirport) {
        int count = airports.size();
        Airport airport;

        Random r = new Random();

        do {
            airport = (Airport) getAirports().get(r.nextInt(count));
        }
        while (currentAirport.equals(airport));

        return airport;
    }

    private void updateGUI() {
        if (gui != null) {
            gui.notifySimHasChanged();
        }
    }

    @Override
    public int getSimTime() {
        return simTime;
    }

    @Override
    public void movePlaneTo(String planeName, GridLocation newLoc) {
        Plane plane = (Plane) getPlane(planeName);
        GridLocation dimension = this.getMapDimensions();

        if ((newLoc.getX() <= dimension.getX() && newLoc.getX() >= 0) && (newLoc.getY() <= dimension.getY() && newLoc.getX() >= 0))
            if (plane != null)
                plane.setLocation(newLoc);
    }

    @Override
    public void start() {
        if (timer == null) {
            timer = new Timer(TIMER_DELAY, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    tick();
                }
            });
            timer.start();
        } else
            timer.start();
    }

    @Override
    public void pause() {
        if (timer.isRunning())
            timer.stop();
    }

    @Override
    public void loadTraffic(InputStream trafficStream) {
        String line;
        String[] planeData = null;
        Plane p;

        // clear all the current aircraft from simulation
        planes.clear();

        try {
            reader = new BufferedReader(new InputStreamReader(trafficStream));

            while ((line = reader.readLine()) != null) {
                planeData = line.split(",");

                if (planeData != null && planeData.length == 5) {
                    p = new Plane(planeData[0]);

                    addPlane(p.getName(), planeData[1], planeData[2]);
                    p.setLocation(new GridLocation(Integer.parseInt(planeData[3]), Integer.parseInt(planeData[4])));
                    p.setSource(getAirport(planeData[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAirports(InputStream airportStream) {
        String line;
        String[] arrTmp = null;

        // clear fixed airports
        airports.clear();

        try {
            reader = new BufferedReader(new InputStreamReader(airportStream));

            while ((line = reader.readLine()) != null && line != "") {
                arrTmp = line.split(",");

                if (arrTmp != null && arrTmp.length == 5) {
                    addAirport(arrTmp[0], arrTmp[1], new GridLocation(Integer.parseInt(arrTmp[2]), Integer.parseInt(arrTmp[3])));
                    getAirport(arrTmp[0]).setTakeOffProb(Double.parseDouble(arrTmp[4]));
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid airport file format");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setReplayLogFile(OutputStream replayFile) {
    }

    @Override
    public void loadFlightPathsForReplay(InputStream replayStream) {
    }

    @Override
    public void setLogFile(OutputStream logStream) {
        FileOutputStream fos = (FileOutputStream) logStream;

        try {
            if (fos != null) {
                fos.write(incidentsBuffer.toString().getBytes());

                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            System.err.println("Error whilst writing file!");
            e.printStackTrace();
        }
    }

    @Override
    public GridLocation getMapDimensions() {
        return mapDimension;
    }

    @Override
    public void setMapDimensions(int x, int y) {
        mapDimension = new GridLocation(x, y);
    }

    @Override
    public void setTickDelay(int millis) {
        TIMER_DELAY = millis;
    }


}
