package ppcSim.sim;

import javafx.application.Platform;
import ppcSim.gui.guiUpdateRunnable;

public class Simulator {

    private SimulatorSettings simulatorSettings;

    private AbstractSun sun;
    private AbstractSetPoint setPoint;
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

    public SimResults runAsync(guiUpdateRunnable callback){

        int controllerQuantity = powerPlants.length;
        String[] controllerNames = new String[controllerQuantity];
        for (int i = 0; i < controllerQuantity; i++) {
            controllerNames[i] = powerPlants[i].getControllerName();
        }

        SimResults simResults = new SimResults(controllerNames);

        double timeStamp = 0;

        while (timeStamp < simulatorSettings.simLength) {

            timeStamp += simulatorSettings.simStepSize;
            double[] irradiance = sun.getIrradiance(timeStamp);
            double plantSetPoint = setPoint.getSetPoint(timeStamp);

            for (int i = 0; i < powerPlants.length; i++) {
                simResults.putPlantDataInstant(i, powerPlants[i].step(timeStamp, irradiance, plantSetPoint)); ;
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    callback.run(simResults);
                }
            });

            try {
                Thread.sleep((long) (simulatorSettings.simStepSize * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        return simResults;
    }

    public SimResults run(){
        int controllerQuantity = powerPlants.length;
        String[] controllerNames = new String[controllerQuantity];

        for (int i = 0; i < controllerQuantity; i++) {
            controllerNames[i] = powerPlants[i].getControllerName();
        }

        SimResults simResults = new SimResults(controllerNames);

        double startingTimeInMillis = java.lang.System.currentTimeMillis();
        double timeStamp = 0;

        while (timeStamp < simulatorSettings.simLength) {
            timeStamp = ( java.lang.System.currentTimeMillis() - startingTimeInMillis ) / 1000;
            double[] irradiance = sun.getIrradiance(timeStamp);
            double plantSetPoint = setPoint.getSetPoint(timeStamp);

            for (int i = 0; i < powerPlants.length; i++) {
                simResults.putPlantDataInstant(i, powerPlants[i].step(timeStamp, irradiance, plantSetPoint)); ;
            }
        }

        return simResults;
    }

}
