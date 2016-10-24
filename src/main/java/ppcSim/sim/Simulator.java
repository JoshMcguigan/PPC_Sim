package ppcSim.sim;

import java.util.Arrays;
import java.util.stream.DoubleStream;

public abstract class Simulator {

    public static PlantData[] simRun(SimulatorSettings simulatorSettings){

        AbstractSun sun;
        AbstractSetPoint setPoint;
        AbstractController controller;
        Inverter[] inverter = new Inverter[simulatorSettings.invQuantity];
        Arrays.fill(inverter, new Inverter(simulatorSettings.invMaxPower, simulatorSettings.invMaxIrr, simulatorSettings.invVariability));
        Substation substation = new Substation(simulatorSettings.substationDeadTime);

        int steps = getStepQuantity(simulatorSettings);
        double simStepSize = simulatorSettings.simStepSize;

        sun = simulatorSettings.sun.get(simulatorSettings);
        setPoint = new ConstantSetPoint(simulatorSettings.plantPowerSetPoint);
        controller = simulatorSettings.controller.get(simulatorSettings);

        PlantData[] plantData = new PlantData[steps];
        int invQuantity = inverter.length;

        double[] powerSetPoints = new double[invQuantity];
        double[] invPower = new double[invQuantity];

        for (int i = 0; i < steps; i++) {
            plantData[i] = simStep(sun.getIrradiance(i*simStepSize), setPoint.getSetPoint(i*simStepSize), i*simStepSize, controller, inverter, substation, powerSetPoints, invPower);
        }

        return plantData;
    }


    private static PlantData simStep(double[] irradiance, double plantPowerSetPoint, double timeStamp, AbstractController controller, Inverter[] inverter, Substation substation, double[] powerSetPoints, double[] invPower){

        double avgIrr = DoubleStream.of(irradiance).sum() / irradiance.length;
        double plantPower = substation.getPlantPower(invPower, timeStamp);
        powerSetPoints = controller.getPowerSetPoints(plantPowerSetPoint, plantPower, invPower, timeStamp);

        for (int i = 0; i < inverter.length; i++) {

            invPower[i] = inverter[i].getPower(powerSetPoints[i], irradiance[i]);

        }

        return new PlantData(plantPowerSetPoint, avgIrr, powerSetPoints, invPower, plantPower, timeStamp);

    }

    public static int getStepQuantity(SimulatorSettings simulatorSettings){
        return (int)(simulatorSettings.simLength/simulatorSettings.simStepSize);
    }
}
