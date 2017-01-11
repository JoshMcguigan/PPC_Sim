package ppcSim.sim;

public class SimulatorSettings {

    public int invQuantity = 20; // quantity of inverters in simulation
    public double simRateMultiplier = 1; // rate at which the simulation runs (multiplier, 1 is real-time)
    public boolean simPause = false;
    public boolean simStop = false;
    public boolean[] invOnline; // allows toggling inverters online and offline

    public SimulatorSettings(){

    }

}
