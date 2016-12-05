package ppcSim.sim;

public class PlantDataInstant {
    public final double plantSetPoint;
    public final double avgIrr;
    public final double[] invPowerSetPoint;
    public final double[] invPowerOutput;
    public final double plantPowerOutput;
    public final double timeStamp;


    PlantDataInstant(double plantSetPoint, double avgIrr, double[] invPowerSetPoint, double[] invPowerOutput, double plantPowerOutput, double timeStamp) {
        this.plantSetPoint = plantSetPoint;
        this.avgIrr = avgIrr;
        this.invPowerSetPoint = invPowerSetPoint;
        this.invPowerOutput = invPowerOutput;
        this.plantPowerOutput = plantPowerOutput;
        this.timeStamp = timeStamp;
    }
}
