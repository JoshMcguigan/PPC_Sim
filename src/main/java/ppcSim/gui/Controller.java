package ppcSim.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ppcSim.sim.*;


public class Controller {

    // Simulation variables
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

    @FXML private LineChart<Double, Double> chart;

    @FXML
    protected void initialize() {

        runSim();

    }


    @FXML protected void runSim(ActionEvent event) {
        runSim();
    }

    private void runSim(){

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

        updateChart(simResults, controllerNames);

    }

    private void updateChart (PlantData[][] plantData, String[] controllerNames){
        ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList();

        // Create set point data set
        LineChart.Series<Double, Double> setPoint = new LineChart.Series<Double, Double>();
        setPoint.setName("Set Point");
        for (int i = 0; i < plantData[0].length; i++) {
            Double x = plantData[0][i].timeStamp;
            Double y = plantData[0][i].plantSetPoint;
            setPoint.getData().add(new XYChart.Data<Double,Double>(x,y));
        }
        lineChartData.add(setPoint);

        // Create plant output data sets
        for (int controller = 0; controller < plantData.length; controller++) {

            LineChart.Series<Double,Double> plantOutput = new LineChart.Series<Double, Double>();
            plantOutput.setName(controllerNames[controller]);

            for (int i = 0; i < plantData[controller].length; i++) {
                Double x = plantData[0][i].timeStamp;
                Double y = plantData[controller][i].plantPowerOutput;
                plantOutput.getData().add(new XYChart.Data<Double,Double>(x,y));
            }

            lineChartData.add(plantOutput);
        }

        chart.setCreateSymbols(false);
        chart.setData(lineChartData);
        chart.createSymbolsProperty();
    }

}
