/**
 * Created by Josh on 10/5/16.
 */
public abstract class Inverter {

    public static double maxPower = 2.2; // Inverter Max Power Output in MW
    public static double maxIrr = 1500; // Irradiance required to produce max power in W/m^2

    public static double getPower(double powerSetPoint, double irr){
        // powerSetPoint specifies maximum power output, as a percent of maxPower
        // irr is current irradiance in W/m^2

        if ( powerSetPoint > 100 || powerSetPoint < 0 ) {
            throw new IllegalArgumentException("Power set point out of range");
        }

        return Math.min(( irr / maxIrr ) * maxPower, maxPower * powerSetPoint);

    }
}
