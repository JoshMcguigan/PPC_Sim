/**
 * Created by Josh on 10/5/16.
 */

import java.util.stream.*;

public class Substation {

    private double plantPower;

    Substation(){
        plantPower = 0;
    }

    public double getPlantPower(double[] invPower){

        // For now, simply return the sum of the inverter power as plant power
        // In the future, it would be possible to add a delay here


        return DoubleStream.of(invPower).sum();

    }
}
