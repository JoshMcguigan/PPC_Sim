import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/6/16.
 */
public class ComplexController extends AbstractController {
    // This is a simple closed loop controller which calculates a single value to send to all inverters
    // Controller steps the power set point in order to move the plant power output in the direction of the set point
    private double maxStepSize = 2.5; // in %
    private double gain = 1;
    private double deadband = .2; // control dead band in MW

    private double powerSetPoint = 50; // in %, sent to every inverter

    ComplexController(int invQuantity, double invPowerMax) {
        super(invQuantity, invPowerMax);

        Arrays.fill(powerSetPoints, powerSetPoint);
    }


    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower){

        // If error is within deadband, control action stays constant
        if (Math.abs(plantPowerSetPoint-currentPlantPower) > deadband ) {
            // If the plant is overproducing, step down the power at the maximum rate
            if (currentPlantPower > plantPowerSetPoint) {
                powerSetPoint -= maxStepSize;
            }
            // If the plant is underproducing, step up the set point based on gain
            else {
                powerSetPoint += Math.min(maxStepSize, (plantPowerSetPoint - currentPlantPower) * gain);
            }
        }

        // Limit set point to between min and max values
        powerSetPoint = Math.max(Math.min(maxPowerSetPoint, powerSetPoint), minPowerSetPoint);

        for (int i = 0; i < powerSetPoints.length; i++) {

            if (powerSetPoints[i] > powerSetPoint){
                powerSetPoints[i] = powerSetPoint;
            } else if ((currentInverterPower[i] / invPowerMax) > (powerSetPoints[i] * .01 * .98)) {
                powerSetPoints[i] = Math.min(powerSetPoint, (currentInverterPower[i]/invPowerMax)*102);
            } else if ((currentInverterPower[i] / invPowerMax) < (powerSetPoints[i]* .01 * .96 )) {
                powerSetPoints[i] *= .94;
            }
        }

        powerSetPoint = Math.min(powerSetPoint, Arrays.stream(powerSetPoints).max().getAsDouble());

        return powerSetPoints;

    }

    public String getControllerName(){
        return "Complex Controller";
    }
}