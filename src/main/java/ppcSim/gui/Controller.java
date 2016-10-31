package ppcSim.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;

import ppcSim.sim.*;

import java.util.ArrayList;
import java.util.List;


public class Controller {

    private SimulatorSettings simulatorSettings;
    private SubstationSettings substationSettings;
    private InverterSettings inverterSettings;
    private SunSettings sunSettings;
    private ControllerSettings controllerSettings;
    private SetPointSettings setPointSettings;

    private Simulator simulator;
    private AbstractSun sun;
    private AbstractSetPoint setPoint;
    private AbstractController controller;
    private Substation substation;
    private Inverter[] inverters;

    private final int secondsPerMinute = 60;

    @FXML private LineChart<Double, Double> chart;

    // Simulation Settings
    @FXML private Slider sliderSimLength;
    @FXML private Slider sliderSimStepSize;

    // Irradiance Settings
    @FXML private Slider sliderIrrBaseLevel;
    @FXML private Slider sliderIrrRange;
    @FXML private Slider sliderIrrCycleTime;
    @FXML private ChoiceBox choiceBoxIrradiancePattern;

    // Set Point Settings
    @FXML private Slider sliderSetPointBaseLevel;
    @FXML private Slider sliderSetPointRange;
    @FXML private Slider sliderSetPointCycleTime;
    @FXML private ChoiceBox choiceBoxSetPointPattern;

    // Controller Settings
    @FXML private Slider sliderControllerExecutionRate;
    @FXML private Slider sliderControllerRampRate;
    @FXML private Slider sliderControllerDeadBand;


    @FXML
    protected void initialize() {

        simulatorSettings = new SimulatorSettings();
        substationSettings = new SubstationSettings();
        inverterSettings = new InverterSettings();
        sunSettings = new SunSettings();
        controllerSettings = new ControllerSettings();
        setPointSettings = new SetPointSettings();

        setupSimulatorSettingsTab();
        setupIrradianceSettingsTab();
        setupSetPointSettingsTab();
        setupControllerSettingsTab();

        runSim();

    }



    @FXML protected void runSim(ActionEvent event) {
        runSim();
    }

    private void runSim(){

        System.out.println("Simulation starting");

        // Create a list of controllers
        List<AbstractController> controllers = new ArrayList<>();
        controllers.add(new NaiveController(controllerSettings ,simulatorSettings.invQuantity, inverterSettings.maxPower));
        controllers.add(new OpenLoopController(controllerSettings, simulatorSettings.invQuantity, inverterSettings.maxPower));
        controllers.add(new ProportionalStepController(controllerSettings, simulatorSettings.invQuantity,
                inverterSettings.maxPower));
        controllers.add(new ComplexController(controllerSettings, simulatorSettings.invQuantity,
                inverterSettings.maxPower));


        // Create 2d array to store results of simulation
        // [controller][step]
        int steps = Simulator.getStepQuantity(simulatorSettings);
        int controllerQuantity = controllers.size();
        PlantData[][] simResults = new PlantData[controllerQuantity][steps];

        // Run simulation once per controller
        String[] controllerNames = new String[controllerQuantity];
        for (int i = 0; i < controllerQuantity; i++) {

            resetSimInstances();
            controller = controllers.get(i);

            simulator = new Simulator(simulatorSettings, substation, sun, setPoint, controller, inverters);
            simResults[i] = simulator.run();

            controllerNames[i] = controller.getControllerName();
        }

        System.out.println("Simulation complete");

        updateChart(simResults, controllerNames);

    }

    private void resetSimInstances(){
        sun = getNewSun();
        setPoint = getNewSetPoint();
        substation = new Substation(substationSettings);
        inverters = Inverter.getArray(inverterSettings, simulatorSettings.invQuantity);
    }

    private AbstractSun getNewSun(){

        String irradiancePattern = (String)choiceBoxIrradiancePattern.getValue();

        switch (irradiancePattern) {
            case "Triangle Wave": return new TriangleWaveSun(sunSettings, simulatorSettings.invQuantity);
            case "Square Wave": return new SquareWaveSun(sunSettings, simulatorSettings.invQuantity);
            case "Sine Wave": return new SineWaveSun(sunSettings, simulatorSettings.invQuantity);
            default: return new TriangleWaveSun(sunSettings, simulatorSettings.invQuantity);
        }

    }

    private AbstractSetPoint getNewSetPoint(){
        String setPointPattern = (String)choiceBoxSetPointPattern.getValue();

        switch(setPointPattern) {
            case "Constant": return new ConstantSetPoint(setPointSettings);
            case "Square Wave": return new SquareWaveSetPoint(setPointSettings);
            default: return new ConstantSetPoint(setPointSettings);
        }
    }

    private void updateChart (PlantData[][] plantData, String[] controllerNames){

        ObservableList<XYChart.Series<Double, Double>> lineChartData = FXCollections.observableArrayList();

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

        // Create set point data set
        LineChart.Series<Double, Double> setPoint = new LineChart.Series<Double, Double>();
        setPoint.setName("Set Point");
        for (int i = 0; i < plantData[0].length; i++) {
            Double x = plantData[0][i].timeStamp / secondsPerMinute;
            Double y = plantData[0][i].plantSetPoint;
            setPoint.getData().add(new XYChart.Data<Double,Double>(x,y));
        }
        lineChartData.add(setPoint);


        chart.setCreateSymbols(false);
        chart.setData(lineChartData);
        chart.createSymbolsProperty();
    }

    private void setupSimulatorSettingsTab(){

        setupSlider(sliderSimLength, 0.0, 60, simulatorSettings.simLength/secondsPerMinute);
        setupSlider(sliderSimStepSize, 0, 5, simulatorSettings.simStepSize);

        sliderSimLength.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                // Allow sim length down to 1 minute only
                simulatorSettings.simLength = Math.max((double)newValue, 1) * 60;
            }
        });
        sliderSimStepSize.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                simulatorSettings.simStepSize = Math.max((double)newValue, 0.5);
            }
        });
    }

    private void setupIrradianceSettingsTab(){

        setupSlider(sliderIrrBaseLevel, 0.0, 1500.0, sunSettings.baseIrr);
        setupSlider(sliderIrrRange, 0.0, 1000.0, sunSettings.range);
        setupSlider(sliderIrrCycleTime, 0.0, 60.0, sunSettings.period/secondsPerMinute);

        sliderIrrBaseLevel.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sunSettings.baseIrr = (double)newValue;
            }
        });
        sliderIrrRange.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sunSettings.range = (double)newValue;
            }
        });
        sliderIrrCycleTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sunSettings.period = Math.max((double)newValue, 1) * secondsPerMinute;
            }
        });
    }

    private void setupSetPointSettingsTab(){

        setupSlider(sliderSetPointBaseLevel, 0.0, simulatorSettings.invQuantity * inverterSettings.maxPower,
                setPointSettings.baseSetPoint);
        setupSlider(sliderSetPointRange, 0.0, simulatorSettings.invQuantity * inverterSettings.maxPower / 2,
                setPointSettings.range);
        setupSlider(sliderSetPointCycleTime, 0.0, 60.0, setPointSettings.period/secondsPerMinute);

        sliderSetPointBaseLevel.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {

                setPointSettings.baseSetPoint = (double)newValue;
            }
        });
        sliderSetPointRange.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setPointSettings.range = (double)newValue;
            }
        });
        sliderSetPointCycleTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setPointSettings.period = Math.max( (double)newValue, 1 ) * secondsPerMinute;
            }
        });
    }

    private void setupControllerSettingsTab(){

        setupSlider(sliderControllerExecutionRate, 5, 30, controllerSettings.executionRate);
        setupSlider(sliderControllerRampRate, 0, 50, controllerSettings.maxRampRate);
        setupSlider(sliderControllerDeadBand, 0, 5, controllerSettings.deadBand);

        sliderControllerExecutionRate.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                controllerSettings.executionRate = (double)newValue;
            }
        });
        sliderControllerRampRate.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                controllerSettings.maxRampRate = Math.max((double)newValue, 1);
            }
        });
        sliderControllerDeadBand.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                controllerSettings.deadBand = (double)newValue;
            }
        });
    }

    private void setupSlider(Slider slider, double minValue, double maxValue, double defaultValue){
        slider.setMin(minValue);
        slider.setMax(maxValue);
        slider.setValue(defaultValue);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(Math.max((int) ((maxValue-minValue) / 4), 1));
    }

}
