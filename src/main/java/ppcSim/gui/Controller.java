package ppcSim.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ppcSim.sim.*;


public class Controller {

    private SimulatorSettings simulatorSettings;
    private final int secondsPerMinute = 60;

    @FXML private LineChart<Double, Double> chart;
    @FXML private Slider sliderPowerSetPoint;
    @FXML private Slider sliderSimLength;

    @FXML
    protected void initialize() {

        simulatorSettings = new SimulatorSettings();

        setupUIElements();

        runSim();

    }



    @FXML protected void runSim(ActionEvent event) {
        runSim();
    }

    private void runSim(){

        System.out.println("Simulation starting");

        // Create list of controllers to test
        List<Class<? extends AbstractController>> controllers;
        controllers = new ArrayList<Class<? extends AbstractController>>(0);
        controllers.add(NaiveController.class);
        controllers.add(OpenLoopController.class);
        controllers.add(ProportionalStepController.class);
        controllers.add(ComplexController.class);

        // Create 2d array to store results of simulation
        // [controller][step]
        int steps = Simulator.getStepQuantity(simulatorSettings);
        PlantData[][] simResults = new PlantData[controllers.size()][steps];

        // Run simulation once per controller
        String[] controllerNames = new String[controllers.size()];
        for (int i = 0; i < controllers.size(); i++) {
            simulatorSettings.controller = controllers.get(i);
            simResults[i] = Simulator.simRun(simulatorSettings);
            try {
                controllerNames[i] = (String)controllers.get(i).getMethod("getControllerName").invoke(null, null);
            } catch (Exception e){
                return;
            }
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
            Double x = plantData[0][i].timeStamp / secondsPerMinute;
            Double y = plantData[0][i].plantSetPoint;
            setPoint.getData().add(new XYChart.Data<Double,Double>(x,y));
        }
        lineChartData.add(setPoint);

        // Create plant output data sets
        for (int controller = 0; controller < plantData.length; controller++) {

            LineChart.Series<Double,Double> plantOutput = new LineChart.Series<Double, Double>();
            plantOutput.setName(controllerNames[controller]);

            for (int i = 0; i < plantData[controller].length; i++) {
                Double x = plantData[0][i].timeStamp / secondsPerMinute;
                Double y = plantData[controller][i].plantPowerOutput;
                plantOutput.getData().add(new XYChart.Data<Double,Double>(x,y));
            }

            lineChartData.add(plantOutput);
        }

        chart.setCreateSymbols(false);
        chart.setData(lineChartData);
        chart.createSymbolsProperty();
    }

    private void setupUIElements(){
        // Set up sliders
        sliderPowerSetPoint.setValue(simulatorSettings.plantPowerSetPoint);
        sliderPowerSetPoint.setMin(0.0);
        sliderPowerSetPoint.setMax(simulatorSettings.invQuantity * simulatorSettings.invMaxPower);
        sliderPowerSetPoint.setShowTickLabels(true);
        sliderPowerSetPoint.setShowTickMarks(true);
        sliderPowerSetPoint.setMajorTickUnit(10);
        sliderPowerSetPoint.setMinorTickCount(5);
        sliderPowerSetPoint.setBlockIncrement(1);

        sliderSimLength.setValue(simulatorSettings.simLength/secondsPerMinute);
        sliderSimLength.setMin(0.0);
        sliderSimLength.setMax(60);
        sliderSimLength.setShowTickLabels(true);
        sliderSimLength.setShowTickMarks(true);
        sliderSimLength.setMajorTickUnit(10);
        sliderSimLength.setMinorTickCount(5);
        sliderSimLength.setBlockIncrement(1);

        // Listen for slider value changes
        sliderPowerSetPoint.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                simulatorSettings.plantPowerSetPoint = (double)newValue;
            }
        });
        sliderSimLength.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                // Allow sim length down to 1 minute only
                simulatorSettings.simLength = Math.max((double)newValue, 1) * 60;
            }
        });
    }

}
