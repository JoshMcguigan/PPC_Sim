import java.util.Arrays;
import java.util.Random;

/**
 * Created by Josh on 10/5/16.
 */

// Class created to provide irradiance values for each inverter
public class Sun {

    private double maxIrr;
    private int invQuantity;
    private double[] irradiance;
    private double irrAvg = maxIrr;
    private Random randomizer;

    Sun(double maxIrr, int invQuantity){
        this.maxIrr = maxIrr;
        this.invQuantity = invQuantity;
        randomizer = new Random();

        irradiance = new double[invQuantity];
        Arrays.fill(irradiance, maxIrr);
    }

    // Logic controlling irradiance levels to each inverter goes here
    public double[] getIrradiance(){
        // Maybe do something with irrAvg here

        // Maybe do something with array of irradiance values here

        // For now, keep it simple and assign every inverter the global irradiance average
        irrAvg = randomizer.nextDouble() * maxIrr;
        Arrays.fill(irradiance, irrAvg);

        return irradiance;
    }
}
