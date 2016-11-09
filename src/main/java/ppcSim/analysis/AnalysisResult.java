package ppcSim.analysis;

public class AnalysisResult {

    private final String controllerName;
    private final double totalEnergyNotIncludingOverProduction;
    private final double greatestInstantaneousOverProduction;
    private final double totalEnergy;

    public AnalysisResult(String controllerName,
                          double energyProductionNotIncludingOverProduction,
                          double greatestInstantaneousOverProduction,
                          double totalEnergy){
        this.controllerName = controllerName;
        this.totalEnergyNotIncludingOverProduction = energyProductionNotIncludingOverProduction;
        this.greatestInstantaneousOverProduction = greatestInstantaneousOverProduction;
        this.totalEnergy = totalEnergy;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getTotalEnergyNotIncludingOverProduction() {
        return formatDoubleAsString(totalEnergyNotIncludingOverProduction);
    }

    public String getGreatestInstantaneousOverProduction() {
        return formatDoubleAsString(greatestInstantaneousOverProduction);
    }

    public String getTotalEnergy() {
        return formatDoubleAsString(totalEnergy);
    }

    private String formatDoubleAsString(double value){
        return String.format("%.3f", value);
    }
}
