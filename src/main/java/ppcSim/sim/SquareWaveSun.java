package ppcSim.sim;

import java.util.stream.DoubleStream;

public class SquareWaveSun extends AbstractSun{

    private double timeLastSwitch; // time stamp of simulation last time state was changed
    private double irradiance;
    private double[] noiseyIrr;
    private boolean irrHigh; // Bit to track whether irradiance is at high or low level
    private double switchTime;

    public SquareWaveSun(SunSettings settings, int invQuantity){

        super(settings, invQuantity);

        irradiance = settings.baseIrr + settings.range;
        irrHigh = true;

        noiseyIrr = new double[invQuantity];

        switchTime = settings.period / 2;

        timeLastSwitch = 0;
    }

    @Override
    public double[] getIrradiance(double timeStamp){

        if ( timeStamp > (timeLastSwitch + switchTime) ){

            timeLastSwitch = timeStamp;

            irrHigh = !irrHigh;

            if (irrHigh){
                irradiance = settings.baseIrr + settings.range;
            } else {
                irradiance = settings.baseIrr - settings.range;
            }

        }

        // Add a little noise to the irradiance value for each inverter
        for (int i = 0; i < noiseyIrr.length; i++) {
            noiseyIrr[i] = irradiance + ( ( randomizer.nextDouble() - 0.5 ) * settings.maxIrr * settings.irrNoiseLevel );

            // add some cloudiness for part of array
            if (i < noiseyIrr.length / 2){
                noiseyIrr[i] -= settings.cloudiness;
            }
        }


        return enforceIrradianceFloor(noiseyIrr);
    }
}