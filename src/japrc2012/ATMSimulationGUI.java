/**
 *
 * Exam number: Y0239881
 *
 */

package japrc2012;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class ATMSimulationGUI extends JFrame implements SimGUIInterface {
    private final JButton tickButton;
    private final JButton exitButton;
    private final JButton startButton;
    private final JButton pauseButton;
    private JCheckBox cbIsPlotVisible;

    private JList<String> planeList;
    private JList<String> airportList;
    private JScrollPane scrollPane;
    private JScrollPane scrollPane_1;
    private JPanel listPanel;

    private JList<String> arrivalList;
    private JList<String> departureList;

    private final Utils utils;

    private ArrayList<PlaneInterface> planesArray;
    private ArrayList<AirportInterface> airportsArray;

    private ArrayList<PlaneInterface> arrivalPlanes;
    private ArrayList<PlaneInterface> departurePlanes;
    private static final Font headerFont = new Font("Tahoma", Font.BOLD, 12);
    File f = new File(Utils.CommonVariables.AIRPROX_INCIDENTS_FILENAME);
    private final MapCanvas canvas;

    private PlaneInterface selectedPlane;
    private boolean isPlotVisible = true;
    private AirportInterface selectedAirport;
    private GridLocation mapDimension;

    private enum planeStatus {
        Leaving, Arriving
    }

    private ATMSimulation sim;
    private JPanel buttonsPanel;
    private JLabel lblAirports;
    private JLabel lblArrival;
    private JLabel lblDepartures;
    private JLabel lblPlanes;
    private JLabel lblSimTime;
    private JPanel tmp;

    private int currentTime = 9;

    public ATMSimulationGUI(ATMSimulation simToUse) {
        super("ATM Simulation GUI");
        getContentPane().setLayout(new FlowLayout(FlowLayout.LEFT));

        this.sim = simToUse;
        buttonsPanel = new JPanel(new FlowLayout());

        startButton = new JButton("Start");
        buttonsPanel.add(startButton);

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        buttonsPanel.add(pauseButton);

        tickButton = new JButton("Tick");
        buttonsPanel.add(tickButton);

        exitButton = new JButton("Exit Program");
        buttonsPanel.add(exitButton);

        cbIsPlotVisible = new JCheckBox("Show plot numbers", true);
        buttonsPanel.add(cbIsPlotVisible);

        lblSimTime = new JLabel("09:00");
        lblSimTime.setFont(new Font("Tahoma", Font.BOLD, 30));
        buttonsPanel.add(lblSimTime);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseButton.setEnabled(true);
                startButton.setEnabled(false);
                tickButton.setEnabled(false);

                sim.start();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });

        tickButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                sim.tick();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseButton.setEnabled(false);
                startButton.setEnabled(true);
                tickButton.setEnabled(true);

                sim.pause();
            }
        });

        cbIsPlotVisible.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                isPlotVisible = cbIsPlotVisible.isSelected();
                canvas.repaint();
            }
        });

        // Plane list
        String[] data = {"one", "two", "three", "four"};

        planeList = new JList<String>(data);
        scrollPane_1 = new JScrollPane(planeList);
        lblPlanes = new JLabel("Planes");
        lblPlanes.setFont(headerFont);
        scrollPane_1.setColumnHeaderView(lblPlanes);
        scrollPane_1.setPreferredSize(new Dimension(200, 380));
        planeList.addListSelectionListener(new PlaneListSelectionListener());

        // a panel holds three Jlists
        listPanel = new JPanel(new GridLayout(1, 3, 7, 5));
//        listPanel = new JPanel(new FlowLayout());

        // AirportList
        airportList = new JList<String>(data);
        airportList.addListSelectionListener(new AirportListSelectionListener());

        scrollPane = new JScrollPane(airportList);

        lblAirports = new JLabel("Airports");
        lblAirports.setFont(headerFont);
        scrollPane.setColumnHeaderView(lblAirports);
        scrollPane.setPreferredSize(new Dimension(230, 150));

        listPanel.add(scrollPane);

        arrivalList = new JList<String>();
        arrivalList.setName(planeStatus.Arriving.toString());
        arrivalList.addListSelectionListener(new PlaneListSelectionListener());
        arrivalList.addFocusListener(new PlaneListFocusListener());
        scrollPane = new JScrollPane(arrivalList);
        scrollPane.setPreferredSize(new Dimension(230, 150));
        listPanel.add(scrollPane);

        lblArrival = new JLabel("Arrival");
        lblArrival.setFont(headerFont);
        scrollPane.setColumnHeaderView(lblArrival);

        departureList = new JList<String>();
        departureList.setName(planeStatus.Leaving.toString());
        departureList.addListSelectionListener(new PlaneListSelectionListener());
        departureList.addFocusListener(new PlaneListFocusListener());
        scrollPane = new JScrollPane(departureList);
        scrollPane.setPreferredSize(new Dimension(230, 150));
        listPanel.add(scrollPane);

        lblDepartures = new JLabel("Departures");
        lblDepartures.setFont(headerFont);
        scrollPane.setColumnHeaderView(lblDepartures);

        add(buttonsPanel);

        // add panel to JFrame
        add(listPanel);

        mapDimension = sim.getMapDimensions();

        canvas = new MapCanvas();
        canvas.addMouseListener(new MapCanvasMouseListener());
        add(canvas);

        tmp = new JPanel(new BorderLayout());
        tmp.setPreferredSize(new Dimension(200, 380));
        tmp.setMaximumSize(new Dimension(200, 380));
        tmp.add(scrollPane_1, BorderLayout.PAGE_START);
        tmp.setBackground(Color.RED);
        add(new JPanel(new FlowLayout()).add(tmp));

        utils = new Utils();

        if (f.exists())
            f.delete();

        // load all airports from file to the simulation first
        sim.loadAirports(utils.readFileToInputStream(Utils.CommonVariables.AIRPORTS_FILENAME));

        // then display them on the GUI (JList)
        this.displayAirports();

        // load traffic from file to the simulation
        sim.loadTraffic(utils.readFileToInputStream(Utils.CommonVariables.TRAFFIC_FILENAME));
    }

    private OutputStream getLogFile() {
        FileOutputStream fs = null;

        try {
            fs = new FileOutputStream(f, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fs;
    }

    @Override
    public void notifySimHasChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateDisplay();
            }
        });
    }

    private void updateDisplay() {
        if (sim == null) {
            // nothing to update from, so do nothing
        } else {

            planesArray = sim.getPlanes();

            // setlogFile
            sim.setLogFile(this.getLogFile());

            // update airports and planes location
            List<String> planeData = new ArrayList<String>();
            for (PlaneInterface p : planesArray) {
                planeData.add(p.getName() + " (" + p.getLocation().getX() + "," + p.getLocation().getY() + ") --> " + p.getDestination().getCode());
            }
            planeList.setListData(planeData.toArray(new String[1]));

            if (selectedAirport != null) {
                airportChanged();
            } else {
                int selectedIndex = airportList.getSelectedIndex();

                if (selectedIndex != -1) {
                    selectedAirport = sim.getAirports().get(selectedIndex);
                    airportChanged();
                }
            }

            lblSimTime.setText(formatSimTime());
            canvas.repaint();
        }
    }

    private String formatSimTime() {
        int simTime = sim.getSimTime();

        if (simTime > 0 && simTime % 3 == 0) {
            currentTime++;
        }

        currentTime = currentTime > 23 ? 0 : currentTime;

        return String.format(Utils.CommonVariables.TIME_FORMAT, currentTime, (simTime % 3) * 20);
    }

    /**
     * Get all the airports from the Simulation and then display in the GUI airport list
     */
    private void displayAirports() {
        airportsArray = sim.getAirports();

        List<String> airportData = new ArrayList<String>();
        for (AirportInterface a : airportsArray) {
            airportData.add(a.getName() + " (" + a.getCode() + ") - " + a.getLocation().getX() + "," + a.getLocation().getY());
        }

        airportList.setListData(airportData.toArray(new String[1]));
    }

    /**
     * Return a list of planes which are moving from or to a specific airport
     *
     * @param airportCode Airport code
     * @param status      Plane's status
     * @return Return a list of planes which are moving from or to a specific airport
     */

    private ArrayList<PlaneInterface> getPlanesDataByAirport(String airportCode, planeStatus status) {

        ArrayList<PlaneInterface> result = new ArrayList<PlaneInterface>();
        planesArray = sim.getPlanes();

        switch (status) {
            case Arriving:
                for (PlaneInterface p : planesArray) {
                    if (p.getDestination().getCode().equals(airportCode))
                        result.add(p);
                }
                break;
            case Leaving:
                for (PlaneInterface p : planesArray) {
                    if (p.getSource().getCode().equals(airportCode))
                        result.add(p);
                }
                break;
        }

        return result;
    }

    /**
     * MapCanvas (inner class) extended from JPanel used for custom drawing
     */

    class MapCanvas extends JPanel {
        private Color color;
        private RescaleOp op;
        private BufferedImage sourceImage, bi;
        private Graphics2D g2d;
        private final float[] scales = {.5f, .5f, .5f, 1f};
        private final float[] offsets = new float[4];
        private GridLocation tmpLoc;

        public MapCanvas() {
            try {
                /**
                 * Drawing image and applying filter based on Oracle tutorial
                 * at:
                 * http://docs.oracle.com/javase/tutorial/2d/images/drawimage.html
                 */
                sourceImage = ImageIO.read(new File(Utils.CommonVariables.MAP_FILENAME));
                bi = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

                op = new RescaleOp(scales, offsets, null);

                Graphics2D g1 = bi.createGraphics();
                g1.drawImage(sourceImage, 0, 0, mapDimension.getX(), mapDimension.getY(), null);
                g1.dispose();

                op.filter(sourceImage, bi);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // set size for the panel
            Dimension size = new Dimension(mapDimension.getX(), mapDimension.getY());
            this.setBackground(new Color(34, 102, 187));
            setPreferredSize(size);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw the background map
            g2d.drawImage(utils.isNightTime(sim.getSimTime()) ? bi : sourceImage, 0, 0, mapDimension.getX(), mapDimension.getY(), null);
            g2d.setStroke(new BasicStroke(2));

            for (AirportInterface a : airportsArray) {
                tmpLoc = utils.convertToPixel(a.getLocation());

                color = (selectedAirport != null && a.getCode().equals(selectedAirport.getCode())) ? Color.RED : Color.BLUE;
                g.setColor(color);

                g2d.drawLine(tmpLoc.getX() - 5, tmpLoc.getY() - 5, tmpLoc.getX() + 5, tmpLoc.getY() + 5);
                g2d.drawLine(tmpLoc.getX() + 5, tmpLoc.getY() - 5, tmpLoc.getX() - 5, tmpLoc.getY() + 5);

                if (isPlotVisible) {
                    g.setColor(Color.BLACK);
                    g2d.drawString(a.getCode(), tmpLoc.getX() + 8, tmpLoc.getY() - 10);
                }
            }

            for (PlaneInterface p : planesArray) {
                tmpLoc = utils.convertToPixel(p.getLocation());
                color = (selectedPlane != null && p.getName().equals(selectedPlane.getName())) ? Color.RED : Color.GREEN;

                g2d.setColor(color);
                g2d.fillOval(tmpLoc.getX() - 3, tmpLoc.getY() - 3, 6, 6);

                if (isPlotVisible) {
                    g.setColor(utils.isNightTime(sim.getSimTime()) ? Color.WHITE : Color.DARK_GRAY);
                    g.drawString(p.getName(), tmpLoc.getX(), tmpLoc.getY() + 15);
                }
            }
        }
    }

    /**
     * AirportListSelectionListener (inner class) used to for listening
     * selection events from the airport list
     */

    class AirportListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                JList<String> list = (JList<String>) e.getSource();
                int selectedIndex = list.getSelectedIndex();

                // testing only
                // System.out.println("Selected Index: " + selectedIndex);

                selectedPlane = null;
                selectedAirport = sim.getAirports().get(selectedIndex);

                airportChanged();
                canvas.repaint();
            }
        }
    }

    /**
     * Update data for both arrival and departure boards every time airport
     * changes.
     */

    private void airportChanged() {
        // arrival list
        arrivalPlanes = getPlanesDataByAirport(selectedAirport.getCode(), planeStatus.Arriving);

        List<String> arrivingData = new ArrayList<String>();

        for (PlaneInterface p : arrivalPlanes) {
            arrivingData.add(p.getName() + " (" + p.getLocation().getX() + "," + p.getLocation().getY() + ") --> " + p.getDestination().getCode());
        }
        arrivalList.setListData(arrivingData.toArray(new String[1]));

        // departed list
        departurePlanes = getPlanesDataByAirport(selectedAirport.getCode(), planeStatus.Leaving);
        List<String> departedData = new ArrayList<String>();
        for (PlaneInterface p : departurePlanes) {
            departedData.add(p.getName() + " (" + p.getLocation().getX() + "," + p.getLocation().getY() + ") --> " + p.getDestination().getCode());
        }
        departureList.setListData(departedData.toArray(new String[1]));

        if (selectedPlane != null) {
            departureList.setSelectedIndex(departurePlanes.indexOf(selectedPlane));
            arrivalList.setSelectedIndex(arrivalPlanes.indexOf(selectedPlane));
            planeList.setSelectedIndex(planesArray.indexOf(selectedPlane));
        }
    }

    class PlaneListSelectionListener implements ListSelectionListener {
        private String[] arr;
        private String selectedVal;

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                JList<String> list = (JList<String>) e.getSource();
                
                selectedVal = list.getSelectedValue();

                if (selectedVal != null && selectedVal.indexOf(" ") != -1) {
                    arr = selectedVal.split(" ");

                    // get plane name here
                    selectedPlane = sim.getPlane(arr[0].trim());
                }

                canvas.repaint();
            }
        }
    }

    class PlaneListFocusListener implements FocusListener {

        @Override
        public void focusGained(FocusEvent e) {
            // To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void focusLost(FocusEvent e) {
            JList<String> source = (JList<String>) e.getSource();
            source.clearSelection();
        }
    }

    class MapCanvasMouseListener implements MouseListener {
        private GridLocation mouseLocation;
        private ListModel<String> currentDepartures;
        private String tmp;
        private String[] tmpArr;

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println(e.getX() / 5 + " - " + e.getY() / 4);
            mouseLocation = new GridLocation(e.getX() / 5, e.getY() / 4);

            for (AirportInterface a : airportsArray) {
                // compare all airport location with selected point in the map

                // if (a.getLocation().equals(mouseLocation)) {
                if (utils.compareLocation(mouseLocation, a.getLocation(), 3)) {
                    airportList.setSelectedIndex(airportsArray.indexOf(a));
                    selectedAirport = a;

                    airportChanged();
                }
            }

            for (PlaneInterface p : planesArray) {
                if (utils.compareLocation(mouseLocation, p.getLocation(), 3)) {

                    selectedAirport = p.getSource();
                    airportList.setSelectedIndex(airportsArray.indexOf(selectedAirport));
                    airportChanged();

                    currentDepartures = departureList.getModel();
                    if (currentDepartures != null && currentDepartures.getSize() > 0) {
                        for (int i = 0; i < currentDepartures.getSize(); i++) {
                            tmp = currentDepartures.getElementAt(i);

                            if (tmp != null && tmp.length() > 0 && tmp.indexOf(" ") != -1) {
                                tmpArr = tmp.split(" ");

                                if (tmpArr.length > 0) {
                                    if (tmpArr[0].trim().equals(p.getName())) {
                                        departureList.setSelectedIndex(i);
                                    }
                                }
                            }
                        }
                    }
                    selectedPlane = p;

                    break;
                }
            }
            
            planeList.setSelectedIndex(planesArray.indexOf(selectedPlane));

            // repaint the map canvas
            canvas.repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public static void main(String[] args) {
        ATMSimulation sim = new ATMSimulation();
        sim.addAirport("TXL", "Berlin Tegel", new GridLocation(1, 3));
        sim.addAirport("LHR", "London Heathrow", new GridLocation(40, 60));
        sim.addAirport("BAV", "Paris Beauvais", new GridLocation(30, 45));

        // set default take of probability for all airport to 1%
        for (AirportInterface a : sim.getAirports()) {
            ((Airport) a).setTakeOffProb(0.01);
        }

        sim.addPlane("TXL_10", "TXL", "LHR");
        sim.addPlane("TXL_11", "TXL", "LHR");
        sim.addPlane("BA1111", "LHR", "BAV");

        ATMSimulationGUI gui = new ATMSimulationGUI(sim);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setSize(720, 620);
        gui.setResizable(false);

//        gui.pack();
        sim.setGUI(gui);
        gui.setLocationRelativeTo(null);
        gui.updateDisplay();
        gui.setVisible(true);
    }
}
