package ppcSim.sim;

public class Simulator {

    public SimulatorSettings simulatorSettings;

    public AbstractSun sun;
    public AbstractSetPoint setPoint;
    private PowerPlant[] powerPlants;

    public Simulator(SimulatorSettings simulatorSettings, SubstationSettings substationSettings,
                     AbstractSun sun, AbstractSetPoint setPoint,
                     AbstractController[] controllers, InverterSettings inverterSettings){

        this.simulatorSettings = simulatorSettings;
        this.sun = sun;
        this.setPoint = setPoint;

        // Create a power plant object for each controller to be tested
        this.powerPlants = new PowerPlant[controllers.length];
        for (int i = 0; i < controllers.length; i++) {
            powerPlants[i] = new PowerPlant(substationSettings, controllers[i], inverterSettings,
                    simulatorSettings.invQuantity);
        }

    }

    public SimResults run(){

        SimResults simResults = new SimResults(powerPlants.length);

        double timeStamp = 0;

        while (timeStamp < simulatorSettings.simLength) {
            timeStamp += simulatorSettings.simStepSize;
            double[] irradiance = sun.getIrradiance(timeStamp);
            double plantSetPoint = setPoint.getSetPoint(timeStamp);

            for (int i = 0; i < powerPlants.length; i++) {
                simResults.putPlantDataInstant(i, powerPlants[i].step(timeStamp, irradiance, plantSetPoint)); ;
            }
        }

        return simResults;
    }

    public static int getStepQuantity(SimulatorSettings simulatorSettings){
        return (int)(simulatorSettings.simLength/simulatorSettings.simStepSize);
    }
}
