package japrc2012test;

import japrc2012.ATMSimulation;
import japrc2012.GridLocation;
import japrc2012.Plane;
import japrc2012.SimGUIInterface;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ATMSimulationBasicTest {
    private ATMSimulation sim;
    private final GridLocation g0 = new GridLocation(0, 0);
    private final GridLocation g13 = new GridLocation(1, 3);
    private final GridLocation g46 = new GridLocation(40, 60);

    private static final class MockSimGUI implements SimGUIInterface {
        public int changeCount;

        @Override
        public void notifySimHasChanged() {
            changeCount++;
        }
    }

    @Before
    public void setUp() throws Exception {
        sim = new ATMSimulation();
        sim.addAirport("TXL", "Berlin Tegel", g13);
        sim.addAirport("LHR", "London Heathrow", g46);
    }

    @Test
    public void testEmptyTick() {
        sim.tick();
    }

    @Test
    public void testGetPlanes() {
        assertEquals(0, sim.getPlanes().size());

        sim.addPlane("BA9081", "TXL", "LHR");
        assertEquals(1, sim.getPlanes().size());
        sim.addPlane("BA9083", "TXL", "LHR");
        assertEquals(2, sim.getPlanes().size());
    }

    @Test
    public void testGetPlane() {
        sim.addPlane("BA9081", "TXL", "LHR");
        Plane p = (Plane) sim.getPlane("BA9081");
        assertEquals("LHR", p.getDestination().getCode());
        assertEquals(g13, p.getLocation());
    }

    @Test
    public void testPlanesMoveOnTick() {
        sim.addPlane("BA9081", "TXL", "LHR");
        Plane p = (Plane) sim.getPlane("BA9081");
        sim.tick();
        assertEquals(new GridLocation(2, 4), p.getLocation());
        sim.tick();
        assertEquals(new GridLocation(3, 5), p.getLocation());
    }

    @Test
    public void testNotifiesGUIOnCommand() {
        MockSimGUI gui = new MockSimGUI();
        sim.setGUI(gui);
        assertEquals(0, gui.changeCount);

        sim.tick();
        assertEquals(1, gui.changeCount);

        sim.tick();
        assertEquals(2, gui.changeCount);
    }
}    

