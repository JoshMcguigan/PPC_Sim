package ppcSim.sim;

import java.util.Arrays;

public class ProportionalStepController extends AbstractTimeBasedController {
    // This is a simple closed loop controller which calculates a single value to send to all inverters
    // Controller steps the power set point in order to move the plant power output in the direction of the set point
    private double gain = 1;

    private double powerSetPoint; // in %, sent to every inverter

    public ProportionalStepController(ControllerSettings settings, int invQuantity, double invPowerMax) {
        super(settings, invQuantity, invPowerMax);

        powerSetPoint = initialPowerSetPoint;
    }


    double[] executeController(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        // If error is within deadband, control action stays constant
        if (Math.abs(plantPowerSetPoint - currentPlantPower) > settings.deadBand || (currentPlantPower>plantPowerSetPoint)) {
            // If the plant is overproducing, step down the power at the maximum rate
            if (currentPlantPower > plantPowerSetPoint) {
                powerSetPoint -= maxStepSizePerExecution;
            }
            // If the plant is underproducing, step up the set point based on gain
            else {
                powerSetPoint += Math.min(maxStepSizePerExecution, (plantPowerSetPoint - currentPlantPower) * gain);
            }
        }

        // Limit set point to between min and max values
        powerSetPoint = Math.max(Math.min(maxPowerSetPoint, powerSetPoint), minPowerSetPoint);

        Arrays.fill(powerSetPoints, powerSetPoint);


        return powerSetPoints;

    }

    public String getControllerName(){
        return "Proportional Step Controller";
    }
}