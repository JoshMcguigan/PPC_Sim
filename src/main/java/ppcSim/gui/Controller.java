package ppcSim.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ppcSim.analysis.AnalysisResult;
import ppcSim.analysis.Analyzer;
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

    private double analysisStartMinute = 0;

    private Simulator simulator;
    private AbstractSun sun;
    private AbstractSetPoint setPoint;

    private final int secondsPerMinute = 60;

    private boolean simActive; // boolean true when there is an active simulation, either playing or paused

    private String[] controllerNames;
    private ObservableList<XYChart.Series<Double, Double>> lineChartData;
    private LineChart.Series<Double, Double> setPointSeries;
    private List<LineChart.Series<Double,Double>> plantOutputSeries;


    // Simulation Settings

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

    // Inverter Settings
    @FXML private CheckBox checkboxToggleInverterOnline;

    // Analysis Settings
    @FXML private Slider sliderAnalysisStartTime;

    // Chart
    @FXML private LineChart<Double, Double> chart;

    // Analysis
    @FXML private TableView tableViewAnalysis;
    @FXML private TableColumn columnController;
    @FXML private TableColumn columnTotalEnergyNotIncludingOverProduction;
    @FXML private TableColumn columnTotalEnergy;
    @FXML private TableColumn columnGreatestInstantaneousOverProduction;


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
        setupAnalysisSettingsTab();

        simActive = false;

    }


    @FXML protected void buttonPlay(ActionEvent event) {
        runSim();
    }

    @FXML protected void buttonPause(ActionEvent event) {
        simulatorSettings.simPause = true;
    }

    @FXML protected void buttonStop(ActionEvent event) {
        shutdown();
    }

    @FXML protected void buttonFastForward(ActionEvent event) {

        double rateMax = 8;

        if (simulatorSettings.simRateMultiplier <= (rateMax / 2) ){
            simulatorSettings.simRateMultiplier *= 2;
        }
    }

    @FXML protected void checkboxToggleInverterOnline(ActionEvent event) {

        simulatorSettings.invOnline[0] = checkboxToggleInverterOnline.isSelected();

    }

    void shutdown(){
        simulatorSettings.simStop = true;
        simActive = false;
    }

    private void runSim(){

        if (!simActive) {

            simActive = true;
            simulatorSettings.simRateMultiplier = 1;

            List<AbstractController> controllers = new ArrayList<>();
            controllers.add(new NaiveController(controllerSettings, simulatorSettings.invQuantity,
                    inverterSettings.maxPower));
            controllers.add(new OpenLoopController(controllerSettings, simulatorSettings.invQuantity,
                    inverterSettings.maxPower));
            controllers.add(new ProportionalStepController(controllerSettings, simulatorSettings.invQuantity,
                    inverterSettings.maxPower));
            controllers.add(new ComplexController(controllerSettings, simulatorSettings.invQuantity,
                    inverterSettings.maxPower));
            controllers.add(new ModbusClientController(controllerSettings, simulatorSettings.invQuantity,
                    inverterSettings.maxPower));

            sun = getNewSun();
            setPoint = getNewSetPoint();

            simulator = new Simulator(simulatorSettings, substationSettings, sun, setPoint,
                    controllers.toArray(new AbstractController[controllers.size()]), inverterSettings);

            initializeChart();

            new Thread(() -> simulator.runAsync(new guiUpdateRunnable() {
                @Override
                public void run(SimResults simResults) {
                    updateChart(simResults);
                    updateAnalysis(simResults.getPlantDataAsArray(), simResults.getControllerNames());
                }
            })).start();

        } else{
            simulatorSettings.simPause = false;
            simulatorSettings.simRateMultiplier = 1;
        }


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

    private void initializeChart(){
        controllerNames = simulator.getControllerNames();
        lineChartData = FXCollections.observableArrayList();

        // Create set point data set
        setPointSeries = new LineChart.Series<Double, Double>();
        setPointSeries.setName("Set Point");
        lineChartData.add(setPointSeries);

        // Create plant output data sets
        plantOutputSeries = new ArrayList<LineChart.Series<Double, Double>>();

        for (int controller = 0; controller < controllerNames.length; controller++) {
            plantOutputSeries.add(new LineChart.Series<Double,Double>());
            plantOutputSeries.get(controller).setName(controllerNames[controller]);
            lineChartData.add(plantOutputSeries.get(controller));
        }

        chart.setCreateSymbols(false);
        chart.setData(lineChartData);
        chart.createSymbolsProperty();

    }

    private void updateChart (SimResults simResults){

        Double x;
        Double y;

        int newestDataIndex = simResults.getPlantDataSize(0) - 1;

        x = simResults.getPlantData(0,newestDataIndex).timeStamp / secondsPerMinute;
        y = simResults.getPlantData(0,newestDataIndex).plantSetPoint;
        setPointSeries.getData().add(new XYChart.Data<Double,Double>(x,y));


        for (int i = 0; i < controllerNames.length; i++) {
            x = simResults.getPlantData(i,newestDataIndex).timeStamp / secondsPerMinute;
            y = simResults.getPlantData(i, newestDataIndex).plantPowerOutput;
            plantOutputSeries.get(i).getData().add(new XYChart.Data<Double,Double>(x,y));
        }

    }

    private void updateAnalysis (PlantDataInstant[][] plantDataInstant, String[] controllerNames){

        Analyzer analyzer = new Analyzer(plantDataInstant, controllerNames, analysisStartMinute * secondsPerMinute);

        tableViewAnalysis.setItems(analyzer.getAnalysisResults());
        columnController.setCellValueFactory(
                new PropertyValueFactory<AnalysisResult, String>("ControllerName"));
        columnTotalEnergyNotIncludingOverProduction.setCellValueFactory(
                new PropertyValueFactory<AnalysisResult, String>("TotalEnergyNotIncludingOverProduction"));
        columnTotalEnergy.setCellValueFactory(
                new PropertyValueFactory<AnalysisResult, String>("TotalEnergy"));
        columnGreatestInstantaneousOverProduction.setCellValueFactory(
                new PropertyValueFactory<AnalysisResult, String>("GreatestInstantaneousOverProduction"));
    }

    private void setupSimulatorSettingsTab(){

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
        setupSlider(sliderControllerRampRate, 0, 50, controllerSettings.targetRampRate);
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
                controllerSettings.targetRampRate = Math.max((double)newValue, 1);
            }
        });
        sliderControllerDeadBand.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                controllerSettings.deadBand = (double)newValue;
            }
        });
    }

    private void setupAnalysisSettingsTab() {

        setupSlider(sliderAnalysisStartTime, 0, 60, analysisStartMinute);

        sliderAnalysisStartTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                analysisStartMinute = (double)newValue;
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
