/**
 * Created by Josh on 10/20/16.
 */
public class ConstantSetPoint extends AbstractSetPoint {

    private double constantSetPoint;

    ConstantSetPoint(double constantSetPoint){
        this.constantSetPoint = constantSetPoint;
    }

    @Override
    public double getSetPoint(double timeStamp) {
        return constantSetPoint;
    }

}
