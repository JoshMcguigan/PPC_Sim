package ppcSim.sim;

import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/5/16.
 */
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

        try {
            sun = simulatorSettings.sun.getConstructor(double.class, int.class).newInstance(simulatorSettings.maxIrr, simulatorSettings.invQuantity);
            setPoint = new ConstantSetPoint(simulatorSettings.plantPowerSetPoint);
        } catch(Exception e){
            System.out.println(e);
            return null;
        }
        try{
            controller = simulatorSettings.controller.getConstructor(int.class, double.class).newInstance(simulatorSettings.invQuantity, simulatorSettings.invMaxPower);
        } catch (Exception e){
            try{
                controller = simulatorSettings.controller.getConstructor(int.class, double.class, double.class).newInstance(simulatorSettings.invQuantity, simulatorSettings.invMaxPower, simulatorSettings.controllerExecutionRate);
            } catch (Exception e2){
                System.out.println(e2);
                return null;
            }
        }

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
