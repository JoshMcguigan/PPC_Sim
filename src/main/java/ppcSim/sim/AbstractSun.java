package ppcSim.sim;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Josh on 10/5/16.
 */

// Class created to provide irradiance values for each inverter
public abstract class AbstractSun extends AbstractSimulationObject{

    protected final double maxIrr;
    protected final int invQuantity;
    protected double[] irradiance;
    protected double irrAvg;
    protected final Random randomizer;

    AbstractSun(double maxIrr, int invQuantity){

        super();

        this.maxIrr = maxIrr;
        this.invQuantity = invQuantity;
        randomizer = new Random();

        irrAvg = maxIrr;

        irradiance = new double[invQuantity];
        Arrays.fill(irradiance, maxIrr);
    }

    // Logic controlling irradiance levels to each inverter goes here
    public abstract double[] getIrradiance(double timeStamp);

}