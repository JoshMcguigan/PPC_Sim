package ppcSim.sim;


public abstract class AbstractTimeBasedController extends AbstractController {

    private double maxStepSize = 2.5; // in % of full plant output per minute
    protected double maxStepSizePerExecution;

    private double executionRate; // Controller execution rate in seconds
    private double lastExecutionTime;



    AbstractTimeBasedController(int invQuantity, double invPowerMax, double executionRate) {
        super(invQuantity, invPowerMax);

        this.executionRate = executionRate;
        maxStepSizePerExecution = ( maxStepSize / 60 ) * executionRate;
        lastExecutionTime = 0;

    }

    public double[] getPowerSetPoints(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp){

        if (timeStamp >= lastExecutionTime + executionRate ) {

            lastExecutionTime = timeStamp;

            powerSetPoints = executeController(plantPowerSetPoint, currentPlantPower, currentInverterPower, timeStamp);

        }

        return powerSetPoints;

    }

    public abstract double[] executeController(double plantPowerSetPoint, double currentPlantPower, double[] currentInverterPower, double timeStamp);

}
