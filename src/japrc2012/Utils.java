/**
 *
 * Exam number: Y0239881
 *
 */

package japrc2012;

import java.io.*;

public class Utils {

    public static final class CommonVariables {
        public static final String AIRPROX_LOG_FORMAT = "%s,%s,%d,%d,%d";
        public static final String TRAFFIC_FILENAME = "traffic.txt";
        public static final String AIRPORTS_FILENAME = "airports.txt";
        public static final String AIRPROX_INCIDENTS_FILENAME = "airprox_log.txt";
        public static final String MAP_FILENAME = "europe.png";
        public static final String TIME_FORMAT = "%02d:%02d";
        public static final int ONE_DAY_TICKS = 72;
    }

    public Utils() {
    }

    /**
     * Convert the grid location to the actual pixel using the provided default ratio (5:4)
     *
     * @param location grid location
     * @return equivalent pixel location
     */
    public GridLocation convertToPixel(GridLocation location) {
        return new GridLocation(location.getX() * 5, location.getY() * 4);
    }

    public void writeFile(OutputStream os, String content) {
        FileOutputStream fos = (FileOutputStream) os;

        try {
            if (fos != null) {
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            System.err.println("Error whilst writing file!");
            e.printStackTrace();
        }
    }

    public InputStream readFileToInputStream(String fileName) {
        InputStream is = null;

        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            System.err.printf("No such file: %s%n", fileName);
        }

        return is;
    }

    public boolean isNightTime(int simTime) {
        // 10PM (from 9AM) = 13h * 3 = 39 ticks
        // 6AM (from 9AM) = 21h * 3 = 63 ticks

        // night time is between 13h and 21h starts from 9AM
        return simTime >= 39 && simTime < 63;
    }

    public boolean compareLocation(GridLocation loc1, GridLocation loc2, int range) {
        return Math.abs(loc1.getX() - loc2.getX()) <= range && Math.abs(loc1.getY() - loc2.getY()) <= range ? true : false;
    }

}
