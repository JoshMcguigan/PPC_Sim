package ppcSim.sim;


public class ConstantSetPoint extends AbstractSetPoint {

    private double constantSetPoint;

    public ConstantSetPoint(double constantSetPoint){
        this.constantSetPoint = constantSetPoint;
    }

    @Override
    public double getSetPoint(double timeStamp) {
        return constantSetPoint;
    }

}
