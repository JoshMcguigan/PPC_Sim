package ppcSim.sim;

import java.util.Arrays;

public abstract class AbstractController {

    protected final int invQuantity;
    protected final double invPowerMax;
    protected final double plantPowerMax;
    protected double[] powerSetPoints;
    protected double maxPowerSetPoint = 100;
    protected double minPowerSetPoint = 10;

    protected AbstractController(int invQuantity, double invPowerMax){
        this.invQuantity = invQuantity;

        // This could be passed as an array to account for different inverter sizes
        this.invPowerMax = invPowerMax;

        plantPowerMax = invQuantity * invPowerMax;

        powerSetPoints = new double[invQuantity];
        Arrays.fill(powerSetPoints, minPowerSetPoint + maxPowerSetPoint / 2);
    }

    public abstract double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp);

    public abstract String getControllerName();
}
