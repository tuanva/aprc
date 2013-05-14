/**
 *
 * Exam number: Y0239881
 *
 */


package japrc2012test;

import japrc2012.ATMSimulation;
import japrc2012.AirportInterface;
import japrc2012.GridLocation;
import japrc2012.Utils;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ATMSimulationLevel2Test {

    private ATMSimulation sim;
    private Utils util;
    private final GridLocation g13 = new GridLocation(1, 3);
    private final GridLocation g46 = new GridLocation(40, 60);

    @Before
    public void setUp() throws Exception {
        sim = new ATMSimulation();
        util = new Utils();

        sim.addAirport("TXL", "Berlin Tegel", g13);
        sim.addAirport("LHR", "London Heathrow", g46);
    }

//    @Test
    public void testNoTakingOffAtNight() {
        int dayCount = 0;
        int nightCount = 0;
        int i = 0;

        for (AirportInterface a : sim.getAirports()) {
            a.setTakeOffProb(0.8);
        }

        while (i <= Utils.CommonVariables.ONE_DAY_TICKS) {
            sim.tick();
            int time = sim.getSimTime();

            dayCount = sim.getPlanes().size();

            if (util.isNightTime(time)) {
                nightCount = sim.getPlanes().size();

                assert nightCount <= dayCount;
            }
            i++;
        }
    }

    @Test
    public void testTrafficLoading() {
        int count = 0;

        BufferedReader br = new BufferedReader(new InputStreamReader(util.readFileToInputStream(Utils.CommonVariables.AIRPORTS_FILENAME)));
        String line;

        try {
            while ((line = br.readLine()) != null && line != "") {
                if (line.indexOf(",") != -1) {
                    // test valid data file format
                    String[] arr = line.split(",");

                    assertEquals(5, arr.length);
                    assertTrue(!arr[0].isEmpty());
                    assertTrue(!arr[1].isEmpty());
                    assertTrue(Pattern.matches("\\d+", arr[2]));
                    assertTrue(Pattern.matches("\\d+", arr[3]));
                    assertTrue(Pattern.matches("([0-9]*)\\.([0-9]*)", arr[4]));

                    count++;
                }
            }

            sim.loadAirports(util.readFileToInputStream(Utils.CommonVariables.AIRPORTS_FILENAME));
            assertEquals(count, sim.getAirports().size());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //reset count
        count = 0;
        br = new BufferedReader(new InputStreamReader(util.readFileToInputStream(Utils.CommonVariables.TRAFFIC_FILENAME)));
        try {
            while ((line = br.readLine()) != null && line != "") {
                if (line.indexOf(",") != -1) {
                    // test valid data file format
                    String[] arr = line.split(",");

                    assertEquals(5, arr.length);
                    assertTrue(!arr[0].isEmpty());
                    assertTrue(!arr[1].isEmpty());
                    assertTrue(!arr[2].isEmpty());
                    assertTrue(Pattern.matches("\\d+", arr[3]));
                    assertTrue(Pattern.matches("\\d+", arr[4]));

                    count++;
                }
            }

            sim.loadTraffic(util.readFileToInputStream(Utils.CommonVariables.TRAFFIC_FILENAME));
            assertEquals(count, sim.getPlanes().size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIncidentsLogging() {
        sim.addAirport("ESB", "Esenboga", new GridLocation(76, 76));
        sim.addAirport("DME", "Moscow", new GridLocation(75, 35));
        sim.addAirport("LHR", "London", new GridLocation(32, 55));

        sim.addPlane("ESB_001", "ESB", "LHR");

        // tick until the incident occurs
        for(int i = 0; i <= 21; i++) {
            sim.tick();

            if(i == 1)
                sim.addPlane("DME_001", "DME", "LHR");

            try {
                sim.setLogFile(new FileOutputStream(new File(Utils.CommonVariables.AIRPROX_INCIDENTS_FILENAME)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(Utils.CommonVariables.AIRPROX_INCIDENTS_FILENAME));
            String line = null;
            String expected = "ESB_001,DME_001,55,55,21";

            while ((line = reader.readLine()) != null) {
                assertEquals(expected, line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
