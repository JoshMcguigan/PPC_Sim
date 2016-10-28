package ppcSim.sim;

import java.util.stream.DoubleStream;

public class TriangleWaveSun extends AbstractSun{

    private double changeRate = 1; // rate of change, in units of (W/m^2)/sec
    private double baseIrr;
    private double range = 100;
    private double irradiance;
    private double[] noiseyIrr;
    private double irrNoiseLevel = .01; // measure of how different the irradiance level to each inverter is
    private double cloudiness = 300; // measure of cloudiness

    public TriangleWaveSun(SunSettings settings, int invQuantity){

        super(settings, invQuantity);

        baseIrr = .5 * settings.maxIrr;

        irradiance = baseIrr;

        noiseyIrr = new double[invQuantity];
    }

    @Override
    public double[] getIrradiance(double timeStamp){

        double timeDelta = getTimeDeltaAndUpdateTimeStamp(timeStamp);

        // Change irradiance baseline by step size
        irradiance += changeRate * timeDelta;

        // If irradiance is too high or low, limit the irradiance value and modify sign of step size
        if ( (irradiance > settings.maxIrr) || (irradiance > baseIrr + range) ){
            irradiance = Math.min(settings.maxIrr, baseIrr + range);
            changeRate *= -1;
        }

        if ( (irradiance < 0 ) || (irradiance < baseIrr - range) ){
            irradiance = Math.max(0.0, baseIrr - range);
            changeRate *= -1;
        }

        // Add a little noise to the irradiance value for each inverter
        for (int i = 0; i < noiseyIrr.length; i++) {
            noiseyIrr[i] = irradiance + ( ( randomizer.nextDouble() - 0.5 ) * settings.maxIrr * irrNoiseLevel );

            // add some cloudiness for part of array
            if (i < noiseyIrr.length / 2){
                noiseyIrr[i] -= cloudiness;
            }
        }


        irrAvg = DoubleStream.of(noiseyIrr).sum() / noiseyIrr.length;

        return noiseyIrr;
    }
}