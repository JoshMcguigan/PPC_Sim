package ppcSim.sim;

public class ComplexController extends AbstractTimeBasedController {

    private double atSetPointDeadBand = 1; // Inverter producing greater than (set point - atSetPointDeadBand) are considered producing at set point, in %
    private double belowSetPointDeadBand = 2; // Inverters producing less than (set point - belowSetPointDeadBand) are considered to be under-producing, in %
    private double maxInverterRampRateMultiplier = 2; // Maximum ramp rate for any individual inverter, as a multiple of the plant target ramp rate

    private boolean[] belowSetPoint; // Used to track which inverters are underperforming
    private boolean[] atSetPoint; // Used to track inverters which are performing at set point
    private int belowSetPointQuantity; // Quantity of inverters which are underperforming
    private int atSetPointQuantity; // Quantity of inverters which are performing at set point
    private double stepUpSize; // Calculated step size for increases in power
    private double stepDownSize; // Calculated step size for decreases in power

    private boolean overProduction; // Flag set when plant is overproducing
    private boolean inDeadBand; // Flag set when plant is in dead band

    public ComplexController(ControllerSettings settings, int invQuantity, double invPowerMax) {

        super(settings, invQuantity, invPowerMax);

        belowSetPoint = new boolean[invQuantity];
        atSetPoint = new boolean[invQuantity];
        overProduction = false;
        inDeadBand = false;

    }


    public double[] executeController(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        // Determine which inverters are underperforming, and which are at set point
        atSetPointQuantity = 0;
        belowSetPointQuantity = 0;
        for (int i = 0; i < invQuantity; i++) {
            if ((currentInverterPower[i] / invPowerMax) * 100 > (powerSetPoints[i] - atSetPointDeadBand)) {
                atSetPoint[i] = true;
                atSetPointQuantity += 1;
            } else {
                atSetPoint[i] = false;
            }
            if ((currentInverterPower[i] / invPowerMax) * 100 < (powerSetPoints[i] - belowSetPointDeadBand)) {
                belowSetPoint[i] = true;
                belowSetPointQuantity += 1;
            } else {
                belowSetPoint[i] = false;
            }
        }

        // Determine plant state
        if ( (currentPlantPower < plantPowerSetPoint - settings.setPointOffset) &&
                (plantPowerSetPoint - settings.setPointOffset - currentPlantPower < settings.deadBand) ){
            inDeadBand = true;
        } else {
            inDeadBand = false;
        }

        overProduction = currentPlantPower > plantPowerSetPoint - settings.setPointOffset;


        // If the plant is overproducing, step down the power
        // Even if the plant is not overproducing, limit inverters set point if they are underproducing
        if (overProduction) {

            // Calculate step down size in order to bring the plant power down to middle of dead band
            stepDownSize = Math.min(maxStepSizePerExecution, ((currentPlantPower - plantPowerSetPoint + settings.setPointOffset + (0.5 * settings.deadBand)) / (invQuantity * invPowerMax)) * 100);

            for (int i = 0; i < invQuantity; i++) {
                // Set the reduced power set point based on actual current production
                powerSetPoints[i] = (currentInverterPower[i] / invPowerMax) * 100 - stepDownSize;
            }
        } else {
            // Limit inverter set points for inverters which are under producing
            // Set the new set point so the current power output is right between the under and over producing limits
            double productionLimitsAverage = ( (atSetPointDeadBand + belowSetPointDeadBand) / 2 ) / 100;
            for (int i = 0; i < invQuantity; i++) {
                if (belowSetPoint[i]) {
                    powerSetPoints[i] = (currentInverterPower[i] / invPowerMax) * 100 / ( 1 - productionLimitsAverage );
                }
            }
        }


        // If plant is producing less than the set point minus dead band, and at least one inverter is performing at set point
        if (!overProduction && !inDeadBand && (atSetPointQuantity > 0)) {

            // Based on number of inverters performing at set point, calculate max step up size
            // This can be higher than configured max step size because it considers that only inverters which are performing
            //      at set point will be given higher set point commands
            // Step size is reduced when current plant power is close to set point, in order to target middle of deadband
            stepUpSize = Math.min(
                    maxStepSizePerExecution,
                    (((plantPowerSetPoint - settings.setPointOffset - currentPlantPower - (settings.deadBand / 2)) / plantPowerMax) * 100)
                    )
                    * Math.min( (invQuantity / atSetPointQuantity), maxInverterRampRateMultiplier );


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

        return powerSetPoints;

    }

    public String getControllerName(){
        return "Complex Controller";
    }
}