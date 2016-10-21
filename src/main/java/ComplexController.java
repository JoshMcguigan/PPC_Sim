import java.util.Arrays;

/**
 * Created by Josh on 10/6/16.
 */
public class ComplexController extends AbstractController {

    private double maxStepSize = 2.5; // in % of full plant output per minute
    private double maxStepSizePerExecution;
    private double deadBand = .2; // control dead band in MW

    private double atSetPointDeadBand = 98; // Inverter must produce at least this fraction of set point to be considered producing at set point, in %
    private double belowSetPointDeadBand = 96; // Inverters producing less than this fraction of set point are considered to be under-producing, in %

    private boolean[] belowSetPoint; // Used to track which inverters are underperforming
    private boolean[] atSetPoint; // Used to track inverters which are performing at set point
    private int belowSetPointQuantity; // Quantity of inverters which are underperforming
    private int atSetPointQuantity; // Quantity of inverters which are performing at set point
    private double stepUpSize; // Calculated step size for increases in power
    private double stepDownSize; // Calculated step size for decreases in power
    private double executionRate; // Controller execution rate in seconds
    private double lastExecutionTime;

    private boolean overProduction; // Flag set when plant is overproducing
    private boolean inDeadBand; // Flag set when plant is in dead band

    ComplexController(int invQuantity, double invPowerMax, double executionRate) {

        super(invQuantity, invPowerMax);

        belowSetPoint = new boolean[invQuantity];
        atSetPoint = new boolean[invQuantity];
        overProduction = false;
        inDeadBand = false;

        // Initialize power controller to set point
        Arrays.fill(powerSetPoints, 50);

        this.executionRate = executionRate;
        maxStepSizePerExecution = ( maxStepSize / 60 ) * executionRate;
        lastExecutionTime = 0;
    }


    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        if (timeStamp >= lastExecutionTime + executionRate ) {

            lastExecutionTime = timeStamp;

            // Determine which inverters are underperforming, and which are at set point
            atSetPointQuantity = 0;
            belowSetPointQuantity = 0;
            for (int i = 0; i < invQuantity; i++) {
                if ((currentInverterPower[i] / invPowerMax) > (powerSetPoints[i] * atSetPointDeadBand * .0001)) {
                    atSetPoint[i] = true;
                    atSetPointQuantity += 1;
                } else {
                    atSetPoint[i] = false;
                }
                if ((currentInverterPower[i] / invPowerMax) < (powerSetPoints[i] * belowSetPointDeadBand * .0001)) {
                    belowSetPoint[i] = true;
                    belowSetPointQuantity += 1;
                } else {
                    belowSetPoint[i] = false;
                }
            }

            // Determine plant state
            inDeadBand = !(plantPowerSetPoint - currentPlantPower > deadBand);
            overProduction = currentPlantPower > plantPowerSetPoint;


            // If the plant is overproducing, step down the power
            // Even if the plant is not overproducing, limit inverters set point if they are underproducing
            if (overProduction) {

                // Calculate step down size in order to bring the plant power down to middle of dead band
                stepDownSize = Math.min(maxStepSizePerExecution, ((currentPlantPower - plantPowerSetPoint + (0.5 * deadBand)) / (invQuantity * invPowerMax)) * 100);

                for (int i = 0; i < invQuantity; i++) {
                    // Set the reduced power set point based on actual current production
                    powerSetPoints[i] = (currentInverterPower[i] / invPowerMax) * 100 - stepDownSize;
                }
            } else {
                // Limit inverter set points for inverters which are under producing
                for (int i = 0; i < invQuantity; i++) {
                    if (belowSetPoint[i]) {
                        powerSetPoints[i] *= belowSetPointDeadBand * .01;
                    }
                }
            }


            // If plant is producing less than the set point minus dead band, and at least one inverter is performing at set point
            if (!overProduction && !inDeadBand && (atSetPointQuantity > 0)) {

                // Based on number of inverters performing at set point, calculate max step up size
                // This can be higher than configured max step size because it considers that only inverters which are performing
                //      at set point will be given higher set point commands
                // Step size is reduced when current plant power is close to set point, in order to target middle of deadband
                stepUpSize = Math.min(maxStepSizePerExecution, ((plantPowerSetPoint - currentPlantPower - (deadBand / 2)) / plantPowerMax) * 100) * (invQuantity / atSetPointQuantity);


                // For inverters which are producing at set point,
                //      step up their power command
                for (int i = 0; i < invQuantity; i++) {
                    if (atSetPoint[i]) {
                        powerSetPoints[i] = ((currentInverterPower[i] / invPowerMax) * 100) + stepUpSize;
                    }
                }
            }

            // Limit set point array to between min and max values
            for (int i = 0; i < invQuantity; i++) {
                powerSetPoints[i] = Math.max(Math.min(maxPowerSetPoint, powerSetPoints[i]), minPowerSetPoint);
            }

        }

        return powerSetPoints;

    }

    public String getControllerName(){
        return "Complex Controller";
    }
}