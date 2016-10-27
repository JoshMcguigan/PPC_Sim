package ppcSim.sim;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public class Simulator {

    public SimulatorSettings settings;
    public AbstractSun sun;
    public AbstractSetPoint setPoint;
    public AbstractController controller;
    public Substation substation;
    public Inverter[] inverters;

    double[] powerSetPoints;
    double[] invPower;

    public Simulator(SimulatorSettings settings, AbstractSun sun, AbstractSetPoint setPoint,
                     AbstractController controller, Substation substation, Inverter[] inverters){
        this.settings = settings;
        this.sun = sun;
        this.setPoint = setPoint;
        this.controller = controller;
        this.substation = substation;
        this.inverters = inverters;
    }

    public Simulator(){
        settings = new SimulatorSettings();
        sun = settings.sun.get(settings);
        setPoint = new ConstantSetPoint(settings.plantPowerSetPoint);
        controller = settings.controller.get(settings);
        substation = new Substation(settings.substationDeadTime);
        inverters = Inverter.getArray(settings.invMaxPower, settings.invMaxIrr,
                settings.invVariability, settings.invQuantity);
    }

    public PlantData[] run(){

        int steps = getStepQuantity(settings);

        PlantData[] plantData = new PlantData[steps];
        int invQuantity = settings.invQuantity;

        powerSetPoints = new double[invQuantity];
        invPower = new double[invQuantity];

        for (int i = 0; i < steps; i++) {
            plantData[i] = step(i);
        }

        return plantData;
    }


    private PlantData step(int currentStep){

        double timeStamp = currentStep*settings.simStepSize;
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
