/**
 *
 * Exam number: Y0239881
 *
 */

package japrc2012;

import java.text.DecimalFormat;
import java.util.Random;

public final class Plane implements PlaneInterface, Comparable<Plane> {
    private String name;
    private GridLocation loc = new GridLocation(0, 0);
    private AirportInterface destination;
    private AirportInterface source;
    private double planeSpeed = 0;
    private double defaultSpeed = 1.00;
    private Random r;
    private DecimalFormat f;

    public Plane(String name) {
        this.name = name;
        r = new Random();
        f = new DecimalFormat("#.###");
    }

    @Override
    public String getName() {
        return name;
    }

    public void setLocation(GridLocation location) {
        loc = location;
    }

    public void setSource(AirportInterface source) {
        this.source = source;
    }

    public void setDestination(AirportInterface airport) {
        destination = airport;
    }

    @Override
    public GridLocation getLocation() {
        return loc;
    }

    @Override
    public AirportInterface getSource() {
        if (source == null) {
            throw new SimulationException("getSource called on Plane " + name + " which has no source");
        } else
            return source;
    }

    @Override
    public AirportInterface getDestination() {
        if (destination == null) {
            throw new SimulationException("getDestination called on Plane " + name + " which has no destination");
        } else {
            return destination;
        }
    }

    @Override
    public void setSpeed(double multiplier) {
        planeSpeed = multiplier;
    }

    @Override
    public void tick() {
        if (destination == null) {
            // circle in place
            // for this sim, we'll treat this as no movement at all
        } else {
            int xMove = destination.getLocation().getX() - loc.getX();
            int yMove = destination.getLocation().getY() - loc.getY();

            xMove = Math.max(xMove, -1);
            xMove = Math.min(xMove, 1);

            yMove = Math.max(yMove, -1);
            yMove = Math.min(yMove, 1);

            if (planeSpeed != 0) {
                planeSpeed = Double.parseDouble(f.format(planeSpeed));

                // make sure plane doesn't go over speed limit
                if (planeSpeed <= defaultSpeed) {
                    double ranNo = Double.parseDouble(f.format(r.nextDouble()));

                    if (ranNo <= planeSpeed)
                        loc = new GridLocation(loc.getX() + xMove, loc.getY() + yMove);
                }
            } else
                loc = new GridLocation(loc.getX() + xMove, loc.getY() + yMove);
        }
    }

    @Override
    public int compareTo(Plane p) {
        return this.getName().compareTo(p.getName());
    }
}
