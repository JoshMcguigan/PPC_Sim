package ppcSim.sim;

public abstract class AbstractSetPoint extends AbstractSimulationObject{

    protected SetPointSettings settings;

    AbstractSetPoint(SetPointSettings settings){
        this.settings = settings;
    }

    abstract double getSetPoint(double timeStamp);
}
