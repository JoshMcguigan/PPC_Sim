import java.util.Random;

/**
 * Created by Josh on 10/5/16.
 */
public class Inverter {

    public final double maxPower; // Inverter Max Power Output in MW
    public final double maxIrr; // Irradiance required to produce max power in W/m^2
    public final double variability; // Measure of maximum variability (two-sided) in power output when limited by set point, in units of percent of max power
    private Random randomizer;

    Inverter(double maxPower, double maxIrr, double variability){

        this.maxPower = maxPower;
        this.maxIrr = maxIrr;
        this.variability = variability;

        randomizer = new Random();

    }

    public double getPower(double powerSetPoint, double irr){
        // powerSetPoint specifies maximum power output, as a percent of maxPower
        // irr is current irradiance in W/m^2

        // It would be possible to add some dead time here, on either irradiance changes, set point changes, or both

        if ( powerSetPoint > 100 || powerSetPoint < 0 ) {
            throw new IllegalArgumentException("Power set point out of range");
        }

        // Calculate set point power limit in MW
        double powerSetPointLimit = maxPower * powerSetPoint;

        // Calculate irradiance power limit in MW
        double irrPowerLimit = ( Math.min(maxIrr, irr) / maxIrr ) * maxPower;

        // Introduce some variability, so if an inverter is limited
        // by the power set point, it doesn't make exactly the set point power
        double realisticPowerSetPointLimit = powerSetPointLimit + ( randomizer.nextDouble() - 0.5 ) * variability * 0.01 * maxPower;

        return Math.min(irrPowerLimit, realisticPowerSetPointLimit);

    }
}
