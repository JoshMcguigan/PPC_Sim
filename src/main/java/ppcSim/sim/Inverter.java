package ppcSim.sim;

import java.util.Arrays;
import java.util.Random;

public class Inverter {

    private InverterSettings settings;

    private final Random randomizer;

    public Inverter(InverterSettings settings){

        this.settings = settings;

        randomizer = new Random();

    }

    public static Inverter[] getArray(InverterSettings settings, int inverterQuantity){
        Inverter[] inverterArray = new Inverter[inverterQuantity];
        Arrays.fill(inverterArray, new Inverter(settings));

        return inverterArray;
    }

    public double getPower(double powerSetPoint, double irr){
        // powerSetPoint specifies maximum power output, as a percent of maxPower
        // irr is current irradiance in W/m^2

        // It would be possible to add some dead time here, on either irradiance changes, set point changes, or both

        if ( powerSetPoint > 100 || powerSetPoint < 0 || irr < 0) {
            throw new IllegalArgumentException("Power set point out of range");
        }

        // Calculate set point power limit in MW
        double powerSetPointLimit = settings.maxPower * powerSetPoint / 100;

        // Calculate irradiance power limit in MW
        double irrPowerLimit = ( Math.min(settings.irrRequiredForMaxPower, irr) / settings.irrRequiredForMaxPower ) * settings.maxPower;

        // Introduce some variability, so if an inverter is limited
        // by the power set point, it doesn't make exactly the set point power
        double realisticPowerSetPointLimit = powerSetPointLimit + ( randomizer.nextDouble() - 0.5 ) * settings.variability * 0.01 * settings.maxPower;

        return Math.min(irrPowerLimit, realisticPowerSetPointLimit);

    }
}
