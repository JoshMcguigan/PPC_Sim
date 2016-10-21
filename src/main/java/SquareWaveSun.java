import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/6/16.
 */
public class SquareWaveSun extends AbstractSun{

    private double period = 200; // number of seconds at each state (high or low)
    private double timeLastSwitch; // time stamp of simulation last time state was changed
    private double baseIrr;
    private double range = 100;
    private double maxIrr;
    private double irradiance;
    private double[] noiseyIrr;
    private double irrNoiseLevel = .01; // measure of how different the irradiance level to each inverter is
    private boolean irrHigh; // Bit to track whether irradiance is at high or low level
    private int cloudiness = 300;

    SquareWaveSun(double maxIrr, int invQuantity){

        super(maxIrr, invQuantity);

        this.maxIrr = maxIrr;

        baseIrr = .5 * maxIrr;

        irradiance = baseIrr + range;
        irrHigh = true;

        noiseyIrr = new double[invQuantity];

        timeLastSwitch = 0;
    }

    @Override
    public double[] getIrradiance(double timeStamp){

        if ( timeStamp > (timeLastSwitch + period) ){

            timeLastSwitch = timeStamp;

            irrHigh = !irrHigh;

            if (irrHigh){
                irradiance = baseIrr + range;
            } else {
                irradiance = baseIrr - range;
            }

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