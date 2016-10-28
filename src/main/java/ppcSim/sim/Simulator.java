package ppcSim.sim;

import java.util.stream.DoubleStream;

public class Simulator {

    public SimulatorSettings simulatorSettings;

    public AbstractSun sun;
    public AbstractSetPoint setPoint;
    public AbstractController controller;
    public Substation substation;
    public Inverter[] inverters;

    double[] powerSetPoints;
    double[] invPower;

    public Simulator(SimulatorSettings simulatorSettings, Substation substation, AbstractSun sun, AbstractSetPoint setPoint,
                     AbstractController controller, Inverter[] inverters){

        this.simulatorSettings = simulatorSettings;

        this.substation = substation;
        this.sun = sun;
        this.setPoint = setPoint;
        this.controller = controller;
        this.inverters = inverters;

    }

    public PlantData[] run(){

        int steps = getStepQuantity(simulatorSettings);

        PlantData[] plantData = new PlantData[steps];
        int invQuantity = simulatorSettings.invQuantity;

        powerSetPoints = new double[invQuantity];
        invPower = new double[invQuantity];

        for (int i = 0; i < steps; i++) {
            plantData[i] = step(i);
        }

        return plantData;
    }


    private PlantData step(int currentStep){

        double timeStamp = currentStep* simulatorSettings.simStepSize;
        double[] irradiance = sun.getIrradiance(timeStamp);
        double plantPowerSetPoint = setPoint.getSetPoint(timeStamp);

        double avgIrr = DoubleStream.of(irradiance).sum() / irradiance.length;
        double plantPower = substation.getPlantPower(invPower, timeStamp);
        powerSetPoints = controller.getPowerSetPoints(plantPowerSetPoint, plantPower, invPower, timeStamp);

        for (int i = 0; i < inverters.length; i++) {
            invPower[i] = inverters[i].getPower(powerSetPoints[i], irradiance[i]);
        }

        return new PlantData(plantPowerSetPoint, avgIrr, powerSetPoints, invPower, plantPower, timeStamp);

    }

    public static int getStepQuantity(SimulatorSettings simulatorSettings){
        return (int)(simulatorSettings.simLength/simulatorSettings.simStepSize);
    }
}
