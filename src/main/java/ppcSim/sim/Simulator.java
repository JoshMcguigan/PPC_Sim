package ppcSim.sim;

import javafx.application.Platform;
import ppcSim.gui.guiUpdateRunnable;

import java.util.Arrays;

public class Simulator {

    private static final int simulatorExecutionRateInMSec = 500;
    private SimulatorSettings simulatorSettings;

    private AbstractSun sun;
    private AbstractSetPoint setPoint;
    private PowerPlant[] powerPlants;
    private boolean simHasRan;

    public Simulator(SimulatorSettings simulatorSettings, SubstationSettings substationSettings,
                     AbstractSun sun, AbstractSetPoint setPoint,
                     AbstractController[] controllers, InverterSettings inverterSettings){

        this.simulatorSettings = simulatorSettings;
        this.sun = sun;
        this.setPoint = setPoint;

        simHasRan = false;

        // initialize inverter online status array with all inverters online
        this.simulatorSettings.invOnline = new boolean[this.simulatorSettings.invQuantity];
        Arrays.fill(this.simulatorSettings.invOnline, true);

        // Create a power plant object for each controller to be tested
        this.powerPlants = new PowerPlant[controllers.length];
        for (int i = 0; i < controllers.length; i++) {
            powerPlants[i] = new PowerPlant(substationSettings, controllers[i], inverterSettings,
                    simulatorSettings.invQuantity);
        }

    }

    public SimResults runAsync(guiUpdateRunnable callback){

        if (simHasRan){
            throw new IllegalStateException("Simulation object can only be ran once.");
        }

        simulatorSettings.simStop = false;
        simulatorSettings.simPause = false;

        SimResults simResults = new SimResults(getControllerNames());

        double lastTimeStamp = java.lang.System.currentTimeMillis();
        double timeStamp = 0;

        while (!simulatorSettings.simStop) {

            if(!simulatorSettings.simPause) {

                timeStamp += (java.lang.System.currentTimeMillis() - lastTimeStamp) *
                        simulatorSettings.simRateMultiplier / 1000;

                double[] irradiance = sun.getIrradiance(timeStamp);
                double plantSetPoint = setPoint.getSetPoint(timeStamp);

                for (int i = 0; i < powerPlants.length; i++) {
                    simResults.putPlantDataInstant(i, powerPlants[i].step(timeStamp, irradiance, plantSetPoint, simulatorSettings.invOnline));
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        callback.run(simResults);
                    }
                });

                lastTimeStamp = java.lang.System.currentTimeMillis();

                try {
                    Thread.sleep((long) (simulatorExecutionRateInMSec));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }else{
                try {
                    lastTimeStamp = java.lang.System.currentTimeMillis();
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        shutdownControllers();
        simHasRan = true;

        return simResults;
    }

    private void shutdownControllers(){
        for (PowerPlant powerPlant: powerPlants) {
            powerPlant.getController().shutdown();
        }
    }

    public String[] getControllerNames(){
        int controllerQuantity = powerPlants.length;
        String[] controllerNames = new String[controllerQuantity];
        for (int i = 0; i < controllerQuantity; i++) {
            controllerNames[i] = powerPlants[i].getControllerName();
        }

        return controllerNames;
    }

}
