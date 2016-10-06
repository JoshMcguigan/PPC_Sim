import java.util.Arrays;
import java.util.Random;

/**
 * Created by Josh on 10/6/16.
 */
public class SimpleRandomSun extends AbstractSun{

    SimpleRandomSun(double maxIrr, int invQuantity){
        super(maxIrr, invQuantity);
    }

    @Override
    public double[] getIrradiance(){

        // Assign every inverter the same random irradiance value

        irrAvg = randomizer.nextDouble() * maxIrr;
        Arrays.fill(irradiance, irrAvg);

        return irradiance;
    }
}
