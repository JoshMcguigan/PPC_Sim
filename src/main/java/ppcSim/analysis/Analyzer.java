package ppcSim.analysis;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ppcSim.sim.PlantDataInstant;

import java.util.ArrayList;
import java.util.List;

public class Analyzer {

    public static final int SECONDS_PER_HOUR = 3600;

    private final PlantDataInstant[][] simResults;
    private final String[] controllerNames;
    private final int controllerQuantity;
    private final double analysisStartTime; // Analysis start time in seconds


    public Analyzer(PlantDataInstant[][] simResults, String[] controllerNames, double analysisStartTime){

        this.simResults = simResults;
        this.controllerNames = controllerNames;
        controllerQuantity = simResults.length;
        this.analysisStartTime = analysisStartTime;

    }

    public ObservableList getAnalysisResults(){
        double[] energyProductionNotIncludingOverProduction = getEnergyProductionNotIncludingOverProduction();
        double[] greatestInstantaneousOverProduction = getGreatestInstantaneousOverProduction();
        double[] totalEnergy = getTotalEnergy();

        List list = new ArrayList();

        for (int i = 0; i < controllerQuantity; i++) {
            list.add(new AnalysisResult(controllerNames[i],
                    energyProductionNotIncludingOverProduction[i],
                    greatestInstantaneousOverProduction[i],
                    totalEnergy[i]));
        }

        return FXCollections.observableList(list);
    }


    private double[] getEnergyProductionNotIncludingOverProduction(){

        // simple rectangle method of approximating integral
        // in cases where the plant output is greater than the set point, only power produced up to the set pont is counted

        double[] totalEnergyProductionPerController = new double[controllerQuantity];

        for (int i = 0; i < controllerQuantity; i++) {
            for (int j = 1; j < simResults[i].length; j++) {
                if (simResults[i][j].timeStamp > analysisStartTime) {
                    totalEnergyProductionPerController[i] +=
                            Math.min(simResults[i][j].plantPowerOutput, simResults[i][j].plantSetPoint) *
                                    (simResults[i][j].timeStamp - simResults[i][j - 1].timeStamp);

                }
            }
            totalEnergyProductionPerController[i] = totalEnergyProductionPerController[i] / SECONDS_PER_HOUR;
        }

        return totalEnergyProductionPerController;
    }

    private double[] getGreatestInstantaneousOverProduction(){

        // Compares plant output to plant set point one step ago

        double[] greatestInstantaneousOverProductionPerController = new double[controllerQuantity];

        for (int i = 0; i < controllerQuantity; i++) {
            for (int j = 1; j < simResults[i].length; j++) {
                if (simResults[i][j].timeStamp > analysisStartTime) {
                    greatestInstantaneousOverProductionPerController[i] =
                            Math.max(greatestInstantaneousOverProductionPerController[i],
                                    simResults[i][j].plantPowerOutput - simResults[i][j-1].plantSetPoint);
                }
            }
        }

        return greatestInstantaneousOverProductionPerController;
    }

    private double[] getTotalEnergy(){

        double[] totalEnergyProductionPerController = new double[controllerQuantity];

        for (int i = 0; i < controllerQuantity; i++) {
            for (int j = 1; j < simResults[i].length; j++) {
                if (simResults[i][j].timeStamp > analysisStartTime) {
                    totalEnergyProductionPerController[i] += simResults[i][j].plantPowerOutput *
                            (simResults[i][j].timeStamp - simResults[i][j - 1].timeStamp);

                }
            }
            totalEnergyProductionPerController[i] = totalEnergyProductionPerController[i] / SECONDS_PER_HOUR;
        }

        return totalEnergyProductionPerController;

    }

}
