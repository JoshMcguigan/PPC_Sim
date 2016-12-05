package ppcSim.sim;

import java.util.Arrays;

public class OpenLoopController extends AbstractTimeBasedController{

    private Double rampedPlantPowerSetPoint;

    public OpenLoopController(ControllerSettings settings, int invQuantity, double invPowerMax) {
        super(settings, invQuantity, invPowerMax);
    }

    double[] executeController(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        calculateRampedPlantPowerSetPoint(plantPowerSetPoint);

        // Simple open loop control of plant power output
        double calculatedPowerSetPoint = ( rampedPlantPowerSetPoint / plantPowerMax ) * 100;
        Arrays.fill(powerSetPoints, Math.min( Math.max(calculatedPowerSetPoint, minPowerSetPoint) , maxPowerSetPoint) );

        return powerSetPoints;

    }

    private void calculateRampedPlantPowerSetPoint(double plantPowerSetPoint){
        if (rampedPlantPowerSetPoint == null){
            rampedPlantPowerSetPoint = plantPowerSetPoint;
        } else{
            if (plantPowerSetPoint > rampedPlantPowerSetPoint){
                rampedPlantPowerSetPoint = Math.min(plantPowerSetPoint, rampedPlantPowerSetPoint + maxStepSizePerExecution);
            }
            if (plantPowerSetPoint < rampedPlantPowerSetPoint){
                rampedPlantPowerSetPoint = Math.max(plantPowerSetPoint, rampedPlantPowerSetPoint - maxStepSizePerExecution);
            }
        }

    }

    public String getControllerName(){
        return "Open Loop Controller";
    }
}
