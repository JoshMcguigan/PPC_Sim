package ppcSim.sim;

import java.util.stream.DoubleStream;

class PowerPlant {

    private final AbstractController controller;
    private final Substation substation;
    private final Inverter[] inverters;

    private double[] powerSetPoints;
    private double[] invPower;

    PowerPlant(SubstationSettings substationSettings,
               AbstractController controller, InverterSettings inverterSettings,
               int invQuantity){

        this.substation = new Substation(substationSettings);
        this.controller = controller;
        this.inverters = Inverter.getArray(inverterSettings, invQuantity);

        powerSetPoints = new double[invQuantity];
        invPower = new double[invQuantity];
    }


    PlantDataInstant step(double timeStamp, double[] irradiance, double plantPowerSetPoint){

        double avgIrr = DoubleStream.of(irradiance).sum() / irradiance.length;
        double plantPower = substation.getPlantPower(invPower, timeStamp);
        powerSetPoints = controller.getPowerSetPoints(plantPowerSetPoint, plantPower, invPower, timeStamp);

        for (int i = 0; i < inverters.length; i++) {
            invPower[i] = inverters[i].getPower(powerSetPoints[i], irradiance[i]);
        }

        return new PlantDataInstant(plantPowerSetPoint, avgIrr, powerSetPoints, invPower, plantPower, timeStamp);

    }

    String getControllerName(){
        return controller.getControllerName();
    }

}
