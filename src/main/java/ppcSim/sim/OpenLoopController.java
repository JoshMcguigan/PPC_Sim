package ppcSim.sim;

import java.util.Arrays;




public class OpenLoopController extends AbstractController{

    public OpenLoopController(ControllerSettings settings, int invQuantity, double invPowerMax) {
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
