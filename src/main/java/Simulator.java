import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/5/16.
 */
public abstract class Simulator {

    public static PlantData[] simRun(AbstractSun sun, AbstractSetPoint setPoint, AbstractController controller, Inverter[] inverter, Substation substation, double simLength, double simStepSize, int steps){

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
}
