package ppcSim.sim;


public class ConstantSetPoint extends AbstractSetPoint {

    public ConstantSetPoint(SetPointSettings settings){
        super(settings);
    }

    @Override
    public double getSetPoint(double timeStamp) {
        return settings.baseSetPoint;
    }

}
