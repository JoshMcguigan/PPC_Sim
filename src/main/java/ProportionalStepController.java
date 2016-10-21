import java.util.Arrays;

/**
 * Created by Josh on 10/6/16.
 */
public class ProportionalStepController extends AbstractController {
    // This is a simple closed loop controller which calculates a single value to send to all inverters
    // Controller steps the power set point in order to move the plant power output in the direction of the set point
    private double maxRampRate = 2.5; // in %/minute
    private double maxRampRatePerExecution;
    private double gain = 1;
    private double deadband = .2; // control dead band in MW
    private double executionRate; // Rate at which the control loop executes, in seconds
    private double lastExecutionTime; // stores the time stamp of last execution

    private double powerSetPoint = 50; // in %, sent to every inverter

    ProportionalStepController(int invQuantity, double invPowerMax , double executionRate) {
        super(invQuantity, invPowerMax);
        this.executionRate = executionRate;

        maxRampRatePerExecution = ( maxRampRate / 60 ) * executionRate;
        lastExecutionTime = 0;
    }


    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        if (timeStamp >= lastExecutionTime + executionRate ) {

            lastExecutionTime = timeStamp;

            // If error is within deadband, control action stays constant
            if (Math.abs(plantPowerSetPoint - currentPlantPower) > deadband || (currentPlantPower>plantPowerSetPoint)) {
                // If the plant is overproducing, step down the power at the maximum rate
                if (currentPlantPower > plantPowerSetPoint) {
                    powerSetPoint -= maxRampRatePerExecution;
                }
                // If the plant is underproducing, step up the set point based on gain
                else {
                    powerSetPoint += Math.min(maxRampRatePerExecution, (plantPowerSetPoint - currentPlantPower) * gain);
                }
            }

            // Limit set point to between min and max values
            powerSetPoint = Math.max(Math.min(maxPowerSetPoint, powerSetPoint), minPowerSetPoint);

            Arrays.fill(powerSetPoints, powerSetPoint);
        }


        return powerSetPoints;

    }

    public String getControllerName(){
        return "Proportional Step Controller";
    }
}