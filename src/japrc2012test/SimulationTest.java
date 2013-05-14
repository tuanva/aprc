package japrc2012test;

import static org.junit.Assert.*;

import java.util.Random;

import japrc2012.ATMSimulation;
import japrc2012.GridLocation;
import japrc2012.Utils;

import org.junit.Before;
import org.junit.Test;

public class SimulationTest {

	private static final String INCIDENT_LOG_FORMAT = "%s,%s,%d,%d,%d";
	private static final String TRAFFIC_FILENAME = "traffic.txt";
	private static final String AIRPORTS_FILENAME = "airports.txt";
	private static final String CHARSET = "US-ASCII";
	private static final String MAP_FILENAME = "europe.png";

	private Utils util;

	ATMSimulation sim;

	@Before
	public void setUp() throws Exception {
		util = new Utils();

		sim = new ATMSimulation();

		// load all airports from file to the simulation first
		sim.loadAirports(util.readInputStream(AIRPORTS_FILENAME));

		// load traffic from file to the simulation
		sim.loadTraffic(util.readInputStream(TRAFFIC_FILENAME));
	}

	@Test
	public void movePlaneTest() {
		// fail("Not yet implemented");

		Random r = new Random();

		for (int i = 0; i < 100; i++) {

			GridLocation loc = new GridLocation(r.nextInt(600), r.nextInt(600));

			sim.movePlaneTo("p221", loc);
		}
	}

}
