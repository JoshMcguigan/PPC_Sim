import java.util.Arrays;

/**
 * Created by Josh on 10/5/16.
 */


public class Controller {

    private int invQuantity;
    private double[] powerSetPoints;
    private double maxPowerSetPoint = 100;
    private double minPowerSetPoint = 0;

    Controller(int invQuantity){
        this.invQuantity = invQuantity;

        powerSetPoints = new double[invQuantity];
        Arrays.fill(powerSetPoints, maxPowerSetPoint);
    }

    public double[] getPowerSetPoints(){
        // Logic to calculate the power set point array goes here
        // Parameters can be added to this function as necessary


        // For now, keep it simple
        Arrays.fill(powerSetPoints, maxPowerSetPoint);


        return powerSetPoints;
    }

}
