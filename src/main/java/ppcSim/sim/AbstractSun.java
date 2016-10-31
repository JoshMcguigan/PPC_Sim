package ppcSim.sim;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;

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

    protected double[] enforceIrradianceFloor(double[] irradiance){
        for (int i = 0; i < irradiance.length; i++) {
            if (irradiance[i]<0){
                irradiance[i] = 0;
            }
        }
        return irradiance;
    }

}
