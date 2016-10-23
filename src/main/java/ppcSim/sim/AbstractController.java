package ppcSim.sim;

import java.util.Arrays;

/**
 * Created by Josh on 10/5/16.
 */


public abstract class AbstractController {

    protected final int invQuantity;
    protected final double invPowerMax;
    protected final double plantPowerMax;
    protected double[] powerSetPoints;
    protected double maxPowerSetPoint = 100;
    protected double minPowerSetPoint = 10;

    AbstractController(int invQuantity, double invPowerMax){
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
