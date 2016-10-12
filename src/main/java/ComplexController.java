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
    private double atSetPointDeadBand = 98; // Inverter must produce at least this fraction of set point to be considered producing at set point, in %
    private double belowSetPointDeadBand = 96; // Inverters producing less than this fraction of set point are considered to be under-producing, in %

    private boolean[] belowSetPoint; // Used to track which inverters are underperforming
    private boolean[] atSetPoint; // Used to track inverters which are performing at set point
    private int belowSetPointQuantity; // Quantity of inverters which are underperforming
    private int atSetPointQuantity; // Quantity of inverters which are performing at set point
    private double stepUpSize; // Calculated step size based on number of inverters online
    private double stepDownSize; // Calculated step down size

    private boolean overProduction; // Flag set when plant is overproducing
    private boolean inDeadBand; // Flag set when plant is in dead band

    ComplexController(int invQuantity, double invPowerMax) {
        super(invQuantity, invPowerMax);

        belowSetPoint = new boolean[invQuantity];
        atSetPoint = new boolean[invQuantity];
        overProduction = false;
        inDeadBand = false;

        Arrays.fill(powerSetPoints, powerSetPoint);
    }


    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower){

        // Determine which inverters are underperforming, and which are at set point
        atSetPointQuantity = 0;
        belowSetPointQuantity = 0;
        for (int i = 0; i < invQuantity; i++) {
            if ((currentInverterPower[i] / invPowerMax) > (powerSetPoints[i] * atSetPointDeadBand * .0001)){
                atSetPoint[i] = true;
                atSetPointQuantity += 1;
            }
            else{
                atSetPoint[i] = false;
            }
            if ((currentInverterPower[i] / invPowerMax) < (powerSetPoints[i] * belowSetPointDeadBand * .0001 )){
                belowSetPoint[i] = true;
                belowSetPointQuantity += 1;
            }
            else{
                belowSetPoint[i] = false;
            }
        }

        // Based on number of inverters performing at set point, calculate step size
        // Only used for steps up, because steps down are sent to all inverters and utilize max step size
        // If zero inverters are at set point, step size isn't used because no inverters will have their power set point increased
        if (atSetPointQuantity != 0) {
            stepUpSize = maxStepSize * (invQuantity / atSetPointQuantity);
        }
        else{
            stepUpSize = maxStepSize;
        }

        // If the plant is overproducing, step down the power
        if (currentPlantPower > plantPowerSetPoint) {
            overProduction = true;
            stepDownSize = Math.min(maxStepSize,((currentPlantPower - plantPowerSetPoint + (0.5 * deadband)) / ( invQuantity * invPowerMax ))*100);
            powerSetPoint -= stepDownSize;
            for (int i = 0; i < invQuantity; i++) {
                // Set the reduced power set point based on actual current production
                powerSetPoints[i] = (currentInverterPower[i] / invPowerMax) * 100 - stepDownSize;
            }
        } else {
            overProduction = false;

            // Check that production is low by more than deadband
            // If so, increase power production by gain*error, or max step size, whichever is less
            if (plantPowerSetPoint - currentPlantPower > deadband) {
                powerSetPoint += Math.min(stepUpSize, (plantPowerSetPoint - currentPlantPower) * gain);
                inDeadBand = false;
            } else{
                inDeadBand = true;
            }
        }

        // Limit master set point to between min and max values
        powerSetPoint = Math.max(Math.min(maxPowerSetPoint, powerSetPoint), minPowerSetPoint);


        // Comment this
        if (!overProduction && !inDeadBand) {
            for (int i = 0; i < invQuantity; i++) {
                if ((currentInverterPower[i] / invPowerMax) > (powerSetPoints[i] * atSetPointDeadBand * .0001)) {
                    powerSetPoints[i] = Math.min(powerSetPoint, ((currentInverterPower[i] / invPowerMax) * 100) + stepUpSize);
                } else if ((currentInverterPower[i] / invPowerMax) < (powerSetPoints[i] * belowSetPointDeadBand * .0001)) {
                    powerSetPoints[i] *= belowSetPointDeadBand * .01;
                }
            }
        }
        powerSetPoint = Math.min(powerSetPoint, Arrays.stream(powerSetPoints).max().getAsDouble());

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