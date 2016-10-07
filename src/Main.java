import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;

import javax.swing.*;

public class Main {

    private static final int invQuantity = 20;
    private static final double maxIrr = 1500;
    private static final int steps = 30;
    private static final double invMaxPower = 2.2;


    public static void main(String[] args) {

        System.out.println("Simulation starting");

        AbstractSun sun = new SimpleRandomSun(maxIrr, invQuantity);
        ControllerSimulator openLoopSim = new ControllerSimulator(invQuantity, maxIrr, invMaxPower, new OpenLoopController(invQuantity, invMaxPower));
        ControllerSimulator naiveSim = new ControllerSimulator(invQuantity,maxIrr,invMaxPower, new NaiveController(invQuantity, invMaxPower));

        double[][] multiIrr = sun.getMultiIrradiance(steps);

        PlantData[] openLoopPlantData = openLoopSim.simRun(multiIrr);
        PlantData[] naivePlantData = naiveSim.simRun(multiIrr);

        PlantData[][] simResults = new PlantData[2][steps];
        simResults[0] = openLoopPlantData;
        simResults[1] = naivePlantData;

        System.out.println("Simulation complete");

        makeChart(simResults);

    }

    private static void makeChart(PlantData[][] plantData) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame("Charts");

                frame.setSize(600, 400);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                DefaultXYDataset ds = new DefaultXYDataset();

                for (int controller = 0; controller < plantData.length; controller++) {

                    double[][] chartData = new double[2][plantData[controller].length];

                    for (int i = 0; i < plantData[controller].length; i++) {
                        chartData[0][i] = i;
                        chartData[1][i] = plantData[controller][i].plantPowerOutput;
                    }

                    ds.addSeries("Plant Power (MW), Controller " + controller, chartData);
                }

                JFreeChart chart = ChartFactory.createXYLineChart("Simulation Results",
                        "Simulation Step", "", ds, PlotOrientation.VERTICAL, true, true,
                        false);

                ChartPanel cp = new ChartPanel(chart);

                frame.getContentPane().add(cp);
            }
        });
    }

}
