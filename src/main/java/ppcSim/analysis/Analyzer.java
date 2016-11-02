package ppcSim.analysis;

import ppcSim.sim.PlantData;

public class Analyzer {

    public static final int SECONDS_PER_HOUR = 3600;

    private final PlantData[][] simResults;
    private final int controllerQuantity;
    private final double analysisStartTime;


    public Analyzer(PlantData[][] simResults, double analysisStartTime){

        this.simResults = simResults;
        controllerQuantity = simResults.length;
        this.analysisStartTime = analysisStartTime;

    }


    public double[] getGreatestInstantaneousOverProductionPerController(){

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

    public double[] getTotalEnergyProductionPerController(){

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

    public double[] getEnergyProductionNotIncludingOverProduction(){

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

}
