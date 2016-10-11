import java.util.Arrays;

/**
 * Created by Josh on 10/6/16.
 */
public class NaiveController extends AbstractController {
    NaiveController(int invQuantity, double invPowerMax) {
        super(invQuantity, invPowerMax);
    }


    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower){

        // Simply sets max power output to all inverters
        Arrays.fill(powerSetPoints, maxPowerSetPoint);

        return powerSetPoints;

    }

    public String getControllerName(){
        return "Naive Controller";
    }
}
