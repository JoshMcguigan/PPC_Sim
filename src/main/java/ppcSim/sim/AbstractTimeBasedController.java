package ppcSim.sim;


abstract class AbstractTimeBasedController extends AbstractController {

    protected ControllerSettings settings;

    protected double maxStepSizePerExecution;

    private double lastExecutionTime;



    AbstractTimeBasedController(ControllerSettings settings, int invQuantity, double invPowerMax) {

        super(invQuantity, invPowerMax);

        this.settings = settings;
        maxStepSizePerExecution = ( settings.targetRampRate / 60 ) * settings.executionRate;
        lastExecutionTime = 0;

    }

    double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        if (timeStamp >= lastExecutionTime + settings.executionRate ) {

            lastExecutionTime = timeStamp;

            powerSetPoints = executeController(plantPowerSetPoint, currentPlantPower, currentInverterPower, timeStamp);

        }

        return powerSetPoints;

    }

    abstract double[] executeController(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp);

}
