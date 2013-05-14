/**
 *
 * Exam number: Y0239881
 *
 */

package japrc2012test;

import japrc2012.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;

public class ATMSimulationLevel1Test {

    private ATMSimulation sim;
    private final GridLocation g13 = new GridLocation(1, 3);
    private final GridLocation g46 = new GridLocation(40, 60);

    @Before
    public void setUp() throws Exception {
        sim = new ATMSimulation();

        sim.addAirport("TXL", "Berlin Tegel", g13);
        sim.addAirport("LHR", "London Heathrow", g46);
    }

    @Test
    public void testLanding() {
        sim.addPlane("BA9081", "TXL", "LHR");

        PlaneInterface p = sim.getPlane("BA9081");
        GridLocation startLoc = p.getSource().getLocation();
        GridLocation desLoc = p.getDestination().getLocation();

        int timeToDestination = desLoc.getY() - startLoc.getY();
        for (int i = 0; i < timeToDestination; i++) {
            sim.tick();
        }

        assertEquals(desLoc, p.getLocation());

        sim.tick();
        p = sim.getPlane("BA9081");

        assertEquals(null, p);
    }

    @Test
    public void testRandomTakingOff() {
        Random r = new Random();
        ArrayList<AirportInterface> airportList = sim.getAirports();
        int tmp;

        AirportInterface a = airportList.get(r.nextInt(airportList.size()));
        tmp = r.nextInt(30);
        a.setTakeOffProb(1);

        while (tmp == 0) {
            tmp = r.nextInt(30);
        }

        for (int i = 0; i < tmp; i++) {
            sim.tick();
        }

        String expectedPlaneName = String.format("%s_%03d", a.getCode(), tmp);
        PlaneInterface p = sim.getPlane(expectedPlaneName);

        if (p == null)
            fail();

//        assertNull(p);
        assertEquals(p.getName(), expectedPlaneName);
    }

    @Test
    public void testRandomAirportSelection() {
        sim.loadAirports(new Utils().readFileToInputStream("airports.txt"));
        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            Airport a = (Airport) sim.getAirports().get(r.nextInt(sim.getAirports().size() - 1));
            assertNotSame(a.getCode(), sim.randomAirport(a));
        }
    }



}
