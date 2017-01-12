package ppcSim.sim;

public class ControllerSettings {

    public double executionRate = 6; // Rate of power plant controller execution, in seconds
    public double targetRampRate = 100; // in % of full plant output per minute
    public double deadBand = 0; // control dead band in MW deadband exists only on the low side of the plant set point
    public double setPointOffset = 0; // allows the controller to target a set point which is below the actual set point in order to reduce overproduction events (MW), can be negative to move deadband above actual set point


    public ControllerSettings(){

    }

}
