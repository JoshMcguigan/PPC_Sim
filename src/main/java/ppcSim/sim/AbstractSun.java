package ppcSim.sim;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Josh on 10/5/16.
 */

// Class created to provide irradiance values for each inverter
public abstract class AbstractSun extends AbstractSimulationObject{

    protected SunSettings settings;

    protected double[] irradiance;
    protected double irrAvg;
    protected final Random randomizer;

    AbstractSun(SunSettings settings, int invQuantity){

        super();

        this.settings = settings;

        randomizer = new Random();

        irrAvg = settings.maxIrr;

        irradiance = new double[invQuantity];
        Arrays.fill(irradiance, settings.maxIrr);
    }

    // Logic controlling irradiance levels to each inverter goes here
    public abstract double[] getIrradiance(double timeStamp);

}
