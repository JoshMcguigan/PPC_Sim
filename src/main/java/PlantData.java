/**
 * Created by Josh on 10/6/16.
 */
public class PlantData {
    public final double plantSetPoint;
    public final double avgIrr;
    public final double[] invPowerSetPoint;
    public final double[] invPowerOutput;
    public final double plantPowerOutput;


    public PlantData(double plantSetPoint, double avgIrr, double[] invPowerSetPoint, double[] invPowerOutput, double plantPowerOutput) {
        this.plantSetPoint = plantSetPoint;
        this.avgIrr = avgIrr;
        this.invPowerSetPoint = invPowerSetPoint;
        this.invPowerOutput = invPowerOutput;
        this.plantPowerOutput = plantPowerOutput;
    }
}
