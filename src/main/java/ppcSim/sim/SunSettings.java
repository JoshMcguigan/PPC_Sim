package ppcSim.sim;

public class SunSettings {

    public double maxIrr = 1500; // maximum irradiance put out by sun (W/m^2)
    public double period = 300; // number of seconds at each state (high or low)
    public double range = 100;  // Irradiance range, on each side of base irradiance (W/M^2)
    public double irrNoiseLevel = .01; // measure of how different the irradiance level to each inverter is
    public int cloudiness = 300;
    public double baseIrr = 750; // Base irradiance value (W/M^2)


    public SunSettings(){

    }

}
