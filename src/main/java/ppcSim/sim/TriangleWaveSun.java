package ppcSim.sim;

import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/6/16.
 */
public class TriangleWaveSun extends AbstractSun{

    private double changeRate = 1; // rate of change, in units of (W/m^2)/sec
    private double baseIrr;
    private double range = 100;
    private double maxIrr;
    private double irradiance;
    private double[] noiseyIrr;
    private double irrNoiseLevel = .01; // measure of how different the irradiance level to each inverter is
    private double cloudiness = 300; // measure of cloudiness

    public TriangleWaveSun(double maxIrr, int invQuantity){

        super(maxIrr, invQuantity);

        this.maxIrr = maxIrr;

        baseIrr = .5 * maxIrr;

        irradiance = baseIrr;

        noiseyIrr = new double[invQuantity];
    }

    @Override
    public double[] getIrradiance(double timeStamp){

        double timeDelta = getTimeDeltaAndUpdateTimeStamp(timeStamp);

        // Change irradiance baseline by step size
        irradiance += changeRate * timeDelta;

        // If irradiance is too high or low, limit the irradiance value and modify sign of step size
        if ( (irradiance > maxIrr) || (irradiance > baseIrr + range) ){
            irradiance = Math.min(maxIrr, baseIrr + range);
            changeRate *= -1;
        }

        if ( (irradiance < 0 ) || (irradiance < baseIrr - range) ){
            irradiance = Math.max(0.0, baseIrr - range);
            changeRate *= -1;
        }

        // Add a little noise to the irradiance value for each inverter
        for (int i = 0; i < noiseyIrr.length; i++) {
            noiseyIrr[i] = irradiance + ( ( randomizer.nextDouble() - 0.5 ) * maxIrr * irrNoiseLevel );

            // add some cloudiness for part of array
            if (i < noiseyIrr.length / 2){
                noiseyIrr[i] -= cloudiness;
            }
        }


        irrAvg = DoubleStream.of(noiseyIrr).sum() / noiseyIrr.length;

        return noiseyIrr;
    }
}