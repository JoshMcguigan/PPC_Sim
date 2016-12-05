package ppcSim.sim;

import java.util.Arrays;


public class NaiveController extends AbstractController {

    public NaiveController(ControllerSettings settings, int invQuantity, double invPowerMax) {
        super(invQuantity, invPowerMax);
    }


    double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        // Simply sets max power output to all inverters
        Arrays.fill(powerSetPoints, maxPowerSetPoint);

        return powerSetPoints;

    }

    public String getControllerName(){
        return "Naive Controller";
    }
}
