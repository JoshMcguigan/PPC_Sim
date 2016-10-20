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
    Figure out why ComplexController is able to ramp up with irradiance changes faster than max ramp rate
    Create sine wave sun
    Test ComplexController for set point step changes with relatively constant irradiance
    Separate display logic from main logic, implement some analytics, possibly some interaction from GUI?
    Confirm set point outputs to each inverters varies over time, ensure set points equalize over time
    Implement ability to simulate inverters going offline (either just loss of comms but still producing or inverter turning completely off)
    Ability to interface with actual plant controller for testing and analysis
    Ability to use real irradiance values from an import file
     */

    /*
    For feature-time-base branch, consider:
    Pre-calculating irradiance arrays will consume way too much memory (FIXED)
    Pre-forming set point arrays will take way too much memory (FIXED)
    Saving data points from every step is probably too much memory (this should be configurable)
    One scan as .1 seconds is more than enough resolution
        this can be configurable, simply have simulator keep track of time and increment as appropriate
    irradiance can update every scan (reduce random effect)
    inverters can run every scan, act on irradiance changes immediately but set point changes have small delay
        this delay accounts for delay in inverter recieving command and implementing it
    substation can have delay which is delay in reading and reporting data
    controllers can act on current information, but only execute every X seconds (3 or 5 would be a decent start)
    for now, continue to just feed in static set point but do it per step (FIXED)
    need to modify chart to show data vs time rather than step
     */



    private static final int invQuantity = 20;
    private static final double maxIrr = 1500;
    private static final int steps = 100;
    private static final double invMaxPower = 2.2;
    private static List<AbstractController> controllers;


    public static void main(String[] args) {

        System.out.println("Simulation starting");

        // Instantiate simulation object
        Simulator sim = new Simulator(invQuantity, invMaxPower);

        // Create list of controllers to test
        controllers = new ArrayList<AbstractController>(0);
        controllers.add(new NaiveController(invQuantity, invMaxPower));
        controllers.add(new OpenLoopController(invQuantity, invMaxPower));
        controllers.add(new ProportionalStepController(invQuantity, invMaxPower));
        controllers.add(new ComplexController(invQuantity, invMaxPower));

        // Create 2d array to store results of simulation
        // [controller][step]
        PlantData[][] simResults = new PlantData[controllers.size()][steps];

        // Run simulation once per controller, with the same irradiance values
        String[] controllerNames = new String[controllers.size()];
        for (int i = 0; i < controllers.size(); i++) {
            simResults[i] = sim.simRun(new TriangleWaveSun(maxIrr, invQuantity), new ConstantSetPoint(19), controllers.get(i), steps);
            controllerNames[i] = controllers.get(i).getControllerName();
        }

        System.out.println("Simulation complete");

        makeChart(simResults, controllerNames);

    }

    private static void makeChart(PlantData[][] plantData, String[] controllerNames) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Charts");

                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                DefaultXYDataset ds = new DefaultXYDataset();

                // Create set point data set
                double[][] chartData = new double[2][plantData[0].length];

                for (int i = 0; i < plantData[0].length; i++) {
                    chartData[0][i] = i;
                    chartData[1][i] = plantData[0][i].plantSetPoint;
                }

                ds.addSeries("Set Point", chartData);


                // Create plant output data sets
                for (int controller = 0; controller < plantData.length; controller++) {

                    chartData = new double[2][plantData[controller].length];

                    for (int i = 0; i < plantData[controller].length; i++) {
                        // x coordinate
                        chartData[0][i] = i;

                        // y coordinate
                        chartData[1][i] = plantData[controller][i].plantPowerOutput;
                    }

                    ds.addSeries(controllerNames[controller], chartData);
                }

                JFreeChart chart = ChartFactory.createXYLineChart("Plant Output (MW) vs. Simulation Step",
                        "Simulation Step", "Plant Output (MW)", ds, PlotOrientation.VERTICAL, true, true,
                        false);

                ChartPanel cp = new ChartPanel(chart);

                frame.getContentPane().add(cp);
            }
        });
    }

}
