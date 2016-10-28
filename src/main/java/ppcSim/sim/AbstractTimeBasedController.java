package ppcSim.sim;


public abstract class AbstractTimeBasedController extends AbstractController {

    private ControllerSettings settings;

    private double maxStepSize = 2.5; // in % of full plant output per minute
    protected double maxStepSizePerExecution;

    private double lastExecutionTime;



    AbstractTimeBasedController(ControllerSettings settings, int invQuantity, double invPowerMax) {

        super(invQuantity, invPowerMax);

        this.settings = settings;
        maxStepSizePerExecution = ( maxStepSize / 60 ) * settings.executionRate;
        lastExecutionTime = 0;

    }

    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        if (timeStamp >= lastExecutionTime + settings.executionRate ) {

            lastExecutionTime = timeStamp;

            powerSetPoints = executeController(plantPowerSetPoint, currentPlantPower, currentInverterPower, timeStamp);

        }

        return powerSetPoints;

    }

    public abstract double[] executeController(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp);

}
