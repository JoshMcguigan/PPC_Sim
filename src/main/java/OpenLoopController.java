import java.util.Arrays;

/**
 * Created by Josh on 10/6/16.
 */
public class OpenLoopController extends AbstractController{

    OpenLoopController(int invQuantity, double invPowerMax) {
        super(invQuantity, invPowerMax);
    }

    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){



        // Simple open loop control of plant power output
        double calculatedPowerSetPoint = ( plantPowerSetPoint / plantPowerMax ) * 100;
        Arrays.fill(powerSetPoints, Math.min( Math.max(calculatedPowerSetPoint, minPowerSetPoint) , maxPowerSetPoint) );

        return powerSetPoints;

    }

    public String getControllerName(){
        return "Open Loop Controller";
    }
}
