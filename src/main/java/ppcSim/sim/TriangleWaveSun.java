package ppcSim.sim;

import java.util.stream.DoubleStream;

public class TriangleWaveSun extends AbstractSun{

    private double changeRate; // rate of change, in units of (W/m^2)/sec
    private double irradiance;
    private double[] noiseyIrr;

    public TriangleWaveSun(SunSettings settings, int invQuantity){

        super(settings, invQuantity);

        changeRate = 4 * settings.range / settings.period;

        irradiance = settings.baseIrr - settings.range;

        noiseyIrr = new double[invQuantity];
    }

    @Override
    public double[] getIrradiance(double timeStamp){

        double timeDelta = getTimeDeltaAndUpdateTimeStamp(timeStamp);

        // Change irradiance baseline by step size
        irradiance += changeRate * timeDelta;

        // If irradiance is too high or low, limit the irradiance value and modify sign of step size
        if ( (irradiance > settings.maxIrr) || (irradiance > settings.baseIrr + settings.range) ){
            irradiance = Math.min(settings.maxIrr, settings.baseIrr + settings.range);
            changeRate *= -1;
        }

        if ( (irradiance < 0 ) || (irradiance < settings.baseIrr - settings.range) ){
            irradiance = Math.max(0.0, settings.baseIrr - settings.range);
            changeRate *= -1;
        }

        // Add a little noise to the irradiance value for each inverter
        for (int i = 0; i < noiseyIrr.length; i++) {
            noiseyIrr[i] = irradiance + ( ( randomizer.nextDouble() - 0.5 ) * settings.maxIrr * settings.irrNoiseLevel );

            // add some cloudiness for part of array
            if (i < noiseyIrr.length / 2){
                noiseyIrr[i] -= settings.cloudiness;
            }
        }


        irrAvg = DoubleStream.of(noiseyIrr).sum() / noiseyIrr.length;

        return enforceIrradianceFloor(noiseyIrr);
    }
}