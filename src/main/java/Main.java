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



    private static final int invQuantity = 20;
    private static final double maxIrr = 1500;
    private static final int steps = 100;
    private static final double invMaxPower = 2.2;
    private static List<AbstractController> controllers;


    public static void main(String[] args) {

        System.out.println("Simulation starting");

        // Instantiate simulation objects

        /*
        Choose your sun type for the simulation
         */
        //AbstractSun sun = new SquareWaveSun(maxIrr, invQuantity);
        AbstractSun sun = new TriangleWaveSun(maxIrr, invQuantity);

        Simulator sim = new Simulator(invQuantity, maxIrr, invMaxPower);

        // Create list of controllers to test
        controllers = new ArrayList<AbstractController>(0);
        controllers.add(new NaiveController(invQuantity, invMaxPower));
        controllers.add(new OpenLoopController(invQuantity, invMaxPower));
        controllers.add(new ProportionalStepController(invQuantity, invMaxPower));
        controllers.add(new ComplexController(invQuantity, invMaxPower));

        // Get irradiance values for each step, for each inverter
        double[][] multiIrr = sun.getMultiIrradiance(steps);

        // Create an array of set point values for the simulation
        // For now, set point kept constant through all steps of simulation
        double[] plantPowerSetPoints = new double[steps];
        Arrays.fill(plantPowerSetPoints, 19);

        // Create 2d array to store results of simulation
        // [controller][step]
        PlantData[][] simResults = new PlantData[controllers.size()][steps];

        // Run simulation once per controller, with the same irradiance values
        String[] controllerNames = new String[controllers.size()];
        for (int i = 0; i < controllers.size(); i++) {
            simResults[i] = sim.simRun(multiIrr, plantPowerSetPoints, controllers.get(i));
            controllerNames[i] = controllers.get(i).getControllerName();
        }

        System.out.println("Simulation complete");

        makeChart(simResults, plantPowerSetPoints, controllerNames);

    }

    private static void makeChart(PlantData[][] plantData, double[] plantPowerSetPoints, String[] controllerNames) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Charts");

                frame.setSize(800, 600);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                DefaultXYDataset ds = new DefaultXYDataset();

                // Create set point data set
                double[][] chartData = new double[2][plantPowerSetPoints.length];

                for (int i = 0; i < plantPowerSetPoints.length; i++) {
                    chartData[0][i] = i;
                    chartData[1][i] = plantPowerSetPoints[i];
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
