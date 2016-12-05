package ppcSim.sim;

public class SineWaveSun extends AbstractSun{

    private double irradiance;
    private double[] noiseyIrr;

    public SineWaveSun(SunSettings settings, int invQuantity){

        super(settings, invQuantity);

        irradiance = settings.baseIrr;

        noiseyIrr = new double[invQuantity];
    }

    @Override
    double[] getIrradiance(double timeStamp){

        irradiance = settings.baseIrr + settings.range * Math.sin(timeStamp * (2*Math.PI) / settings.period);

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