package japrc2012;


import java.text.DecimalFormat;
import java.util.Random;

public final class Airport implements AirportInterface {
    private String code;
    private String name;
    private GridLocation loc;
    private double takeOfProb = 0.0;
    private Random r;
    private double ranNo;
    private DecimalFormat formatter;
    private int planeCounter = 0;

    public Airport(String code, String name, GridLocation location) {
        this.code = code;
        this.name = name;
        loc = location;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GridLocation getLocation() {
        return loc;
    }

    @Override
    public boolean getOpen() {
        return false;
    }

    @Override
    public void tick() {
        planeCounter++;
    }

    @Override
    public void setTakeOffProb(double prob) {
        this.takeOfProb = prob;
    }

    public double getTakeOfProb() {
        return this.takeOfProb;
    }

    public String getPlaneCounter() {
        return String.format("%03d", planeCounter);
    }

    public boolean canTakeOff() {
        if (this.takeOfProb == 1)
            return true;

        formatter = new DecimalFormat("#.###");
        r = new Random();

        ranNo = Double.parseDouble(formatter.format(r.nextDouble()));
        return ranNo <= Double.parseDouble(formatter.format(this.takeOfProb)) && ranNo > 0.00;
    }

}
