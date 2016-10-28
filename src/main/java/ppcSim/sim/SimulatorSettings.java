package ppcSim.sim;

public class SimulatorSettings {

    public int invQuantity = 20; // quantity of inverters in simulation
    public double maxIrr = 1500; // maximum irradiance put out by sun (W/m^2)
    public double simLength = 600; // simulation time in seconds
    public double simStepSize = .5; // simulation step size in seconds
    public double controllerExecutionRate = 6; // Rate of power plant controller execution, in seconds
    public double plantPowerSetPoint = 17.5; // plant power set point (MW)

    public SimulatorSettings(){

    }

}
