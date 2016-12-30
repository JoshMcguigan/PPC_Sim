package ppcSim.sim;

public class ControllerSettings {

    public double executionRate = 6; // Rate of power plant controller execution, in seconds
    public double targetRampRate = 25; // in % of full plant output per minute
    public double deadBand = .2; // control dead band in MW


    public ControllerSettings(){

    }

}
