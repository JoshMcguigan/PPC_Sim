package ppcSim.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Josh on 10/23/16.
 */
public class SimulatorSettings {

    // default simulation parameters
    public Class<? extends AbstractSun> sun = TriangleWaveSun.class;
    public Class<? extends AbstractController> controller = NaiveController.class;
    public int invQuantity = 20; // quantity of inverters in simulation
    public double maxIrr = 1500; // maximum irradiance put out by sun (W/m^2)
    public double simLength = 600; // simulation time in seconds
    public double simStepSize = .5; // simulation step size in seconds
    public double controllerExecutionRate = 6; // Rate of power plant controller execution, in seconds
    public double invMaxPower = 2.2; // maximum power per inverter (MW)
    public double invMaxIrr = 1400; // irradiance required by inverters to output max power
    public double invVariability = 0.5; // variability in inverter power when limited by set point (% of maximum power)
    public double plantPowerSetPoint = 17.5; // plant power set point (MW)
    public double substationDeadTime = 2.0; // time delay in substation power measurement (seconds)


    public SimulatorSettings(){




    }

}
