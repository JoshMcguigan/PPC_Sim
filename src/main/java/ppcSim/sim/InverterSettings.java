package ppcSim.sim;

public class InverterSettings {

    public double maxPower = 2.2; // maximum power per inverter (MW)
    public double irrRequiredForMaxPower = 1400; // irradiance required by inverters to output max power (w/m^2)
    public double variability = 0.2; // variability in inverter power when limited by set point (% of maximum power)

    public InverterSettings(){

    }

}
