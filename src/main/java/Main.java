import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    /*
    Todo
    Modify sun class constructors allow configuration variables to be passed in
    Create inverter group class which contains array of inverters
    move current graphics into a class of its own
    Create step change set point
    Create sine wave sun
    Test ComplexController for set point step changes with relatively constant irradiance
    Separate display logic from main logic, implement some analytics, possibly some interaction from GUI?
    Confirm set point outputs to each inverters varies over time, ensure set points equalize over time
    Implement ability to simulate inverters going offline (either just loss of comms but still producing or inverter turning completely off)
    Ability to interface with actual plant controller for testing and analysis
    Ability to use real irradiance values from an import file
     */


    // SIMULATION CONFIGURATION PARAMETERS
    private static final int invQuantity = 20; // quantity of inverters in simulation
    private static final double maxIrr = 1500; // maximum irradiance put out by sun (W/m^2)
    private static final double simLength = 600; // simulation time in seconds
    private static final double simStepSize = .5; // simulation step size in seconds
    private static final double controllerExecutionRate = 6; // Rate of power plant controller execution, in seconds
    private static final double invMaxPower = 2.2; // maximum power per inverter (MW)
    private static final double invMaxIrr = 1400; // irradiance required by inverters to output max power
    private static final double invVariability = 0.5; // variability in inverter power when limited by set point (% of maximum power)
    private static final double plantPowerSetPoint = 19; // plant power set point (MW)
    private static final double substationDeadTime = 2.0; // time delay in substation power measurement (seconds)


    private static final int steps = (int)(simLength/simStepSize);
    private static List<AbstractController> controllers;
    private static Inverter[] inverter;
    private static Substation substation;


    public static void main(String[] args) {

        System.out.println("Simulation starting");

        // Instantiate simulation objects
        inverter = new Inverter[invQuantity];
        Arrays.fill(inverter, new Inverter(invMaxPower, invMaxIrr, invVariability));
        substation = new Substation(substationDeadTime);

        // Create list of controllers to test
        controllers = new ArrayList<AbstractController>(0);
        controllers.add(new NaiveController(invQuantity, invMaxPower));
        controllers.add(new OpenLoopController(invQuantity, invMaxPower));
        controllers.add(new ProportionalStepController(invQuantity, invMaxPower, controllerExecutionRate));
        controllers.add(new ComplexController(invQuantity, invMaxPower, controllerExecutionRate));

        // Create 2d array to store results of simulation
        // [controller][step]
        PlantData[][] simResults = new PlantData[controllers.size()][steps];

        // Run simulation once per controller
        String[] controllerNames = new String[controllers.size()];
        for (int i = 0; i < controllers.size(); i++) {
            simResults[i] = Simulator.simRun(new TriangleWaveSun(maxIrr, invQuantity),
                    new ConstantSetPoint(plantPowerSetPoint),
                    controllers.get(i),
                    inverter,
                    substation,
                    simLength,
                    simStepSize,
                    steps);
            controllerNames[i] = controllers.get(i).getControllerName();
        }

        System.out.println("Simulation complete");

        makeChart(simResults, controllerNames);

    }

    private static void makeChart(PlantData[][] plantData, String[] controllerNames) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("PPC Simulator");

                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                DefaultXYDataset ds = new DefaultXYDataset();

                // Create set point data set
                double[][] chartData = new double[2][plantData[0].length];

                // chartData[0=x,1=y][data point instance]
                for (int i = 0; i < plantData[0].length; i++) {
                    chartData[0][i] = plantData[0][i].timeStamp;
                    chartData[1][i] = plantData[0][i].plantSetPoint;
                }

                ds.addSeries("Set Point", chartData);


                // Create plant output data sets
                for (int controller = 0; controller < plantData.length; controller++) {

                    chartData = new double[2][plantData[controller].length];

                    for (int i = 0; i < plantData[controller].length; i++) {
                        // x coordinate
                        chartData[0][i] = plantData[0][i].timeStamp;

                        // y coordinate
                        chartData[1][i] = plantData[controller][i].plantPowerOutput;
                    }

                    ds.addSeries(controllerNames[controller], chartData);
                }

                JFreeChart chart = ChartFactory.createXYLineChart("Plant Output (MW) vs. Time (sec)",
                        "Time (sec)", "Plant Output (MW)", ds, PlotOrientation.VERTICAL, true, true,
                        false);

                ChartPanel cp = new ChartPanel(chart);

                frame.getContentPane().add(cp);
            }
        });
    }

}
