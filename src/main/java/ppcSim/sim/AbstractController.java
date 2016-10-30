package ppcSim.sim;

import java.util.Arrays;

public abstract class AbstractController {

    protected final int invQuantity;
    protected final double invPowerMax;
    protected final double plantPowerMax;
    protected double[] powerSetPoints;
    protected double maxPowerSetPoint = 100;
    protected double minPowerSetPoint = 5;
    protected double initialPowerSetPoint;

    protected AbstractController(int invQuantity, double invPowerMax){
        this.invQuantity = invQuantity;

        // This could be passed as an array to account for different inverter sizes
        this.invPowerMax = invPowerMax;

        plantPowerMax = invQuantity * invPowerMax;

        initialPowerSetPoint = ( minPowerSetPoint + maxPowerSetPoint ) / 2;

        powerSetPoints = new double[invQuantity];
        Arrays.fill(powerSetPoints, initialPowerSetPoint);
    }

    public abstract double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp);

    public abstract String getControllerName();
}
