package ppcSim.sim;

/**
 * Created by Josh on 10/20/16.
 */
public abstract class AbstractSetPoint extends AbstractSimulationObject{
    public abstract double getSetPoint(double timeStamp);
}
