import java.util.Arrays;
import java.util.Random;
import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/6/16.
 */
public class SimpleRandomSun extends AbstractSun{

    SimpleRandomSun(double maxIrr, int invQuantity){
        super(maxIrr, invQuantity);
    }

    @Override
    public double[] getIrradiance(){

        // Assign every inverter it's own random irradiance

        for (int i = 0; i < irradiance.length; i++) {
            irradiance[i] = randomizer.nextDouble() * maxIrr;
        }

        irrAvg = DoubleStream.of(irradiance).sum() / irradiance.length;

        return irradiance;
    }
}
