package ppcSim.sim;

public class TriangleWaveSun extends AbstractSun{

    private double changeRate; // rate of change, in units of (W/m^2)/sec
    private double irradiance;
    private double[] noiseyIrr;
    private boolean increasing; // boolean tracks whether the triangle wave is increasing or decreasing

    public TriangleWaveSun(SunSettings settings, int invQuantity){

        super(settings, invQuantity);

        irradiance = settings.baseIrr - settings.range;

        noiseyIrr = new double[invQuantity];

        increasing = true;
    }

    @Override
    double[] getIrradiance(double timeStamp){

        double timeDelta = getTimeDeltaAndUpdateTimeStamp(timeStamp);

        changeRate = 4 * settings.range / settings.period;

        // Change irradiance baseline by step size
        if (increasing) {
            irradiance += changeRate * timeDelta;
        } else{
            irradiance -= changeRate * timeDelta;
        }
        // If irradiance is too high or low, limit the irradiance value and modify sign of step size
        if ( (irradiance > settings.maxIrr) || (irradiance > settings.baseIrr + settings.range) ){
            irradiance = Math.min(settings.maxIrr, settings.baseIrr + settings.range);
            increasing = !increasing;
        }

        if ( (irradiance < 0 ) || (irradiance < settings.baseIrr - settings.range) ){
            irradiance = Math.max(0.0, settings.baseIrr - settings.range);
            increasing = !increasing;
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