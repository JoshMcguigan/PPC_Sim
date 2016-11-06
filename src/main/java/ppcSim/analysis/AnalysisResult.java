package ppcSim.analysis;

public class AnalysisResult {

    private final String controllerName;
    private final double totalEnergyNotIncludingOverProduction;

    public AnalysisResult(String controllerName, double totalEnergy){
        this.controllerName = controllerName;
        this.totalEnergyNotIncludingOverProduction = totalEnergy;
    }

    public String getControllerName() {
        return controllerName;
    }

    public String getTotalEnergyNotIncludingOverProduction() {
        return String.format("%.3f", totalEnergyNotIncludingOverProduction);
    }
}
