import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/5/16.
 */
public class Simulator {

    private final int invQuantity;
    private final double invMaxIrr = 1400;
    private final double invVariability = 1;
    private double[] powerSetPoints;
    private double[] invPower;

    private Inverter[] inverter;
    private AbstractController controller;
    private Substation substation;

    private static double simLength; // simulation time in seconds
    private static double simStepSize; // simulation step size in seconds
    private static int steps;

    Simulator(int invQuantity, double invMaxPower, double simLength, double simStepSize, int steps){

        this.invQuantity = invQuantity;

        powerSetPoints = new double[invQuantity];
        invPower = new double[invQuantity];

        inverter = new Inverter[invQuantity];
        substation = new Substation();

        // Instantiate the inverter array with identical inverters
        Arrays.fill(inverter, new Inverter(invMaxPower, invMaxIrr, invVariability));

        this.simLength = simLength;
        this.simStepSize = simStepSize;
        this.steps = steps;

    }

    public PlantData[] simRun(AbstractSun sun, AbstractSetPoint setPoint, AbstractController controller, int steps){

        this.controller = controller;

        PlantData[] plantData = new PlantData[steps];

        for (int i = 0; i < steps; i++) {
            plantData[i] = simStep(sun.getIrradiance(i*simStepSize), setPoint.getSetPoint(i*simStepSize), i*simStepSize);
        }

        return plantData;
    }


    private PlantData simStep(double[] irradiance, double plantPowerSetPoint, double timeStamp){

        double avgIrr = DoubleStream.of(irradiance).sum() / irradiance.length;
        double plantPower = substation.getPlantPower(invPower);
        powerSetPoints = controller.getPowerSetPoints(plantPowerSetPoint, plantPower, invPower);

        for (int i = 0; i < invQuantity; i++) {

            invPower[i] = inverter[i].getPower(powerSetPoints[i], irradiance[i]);

        }

        return new PlantData(plantPowerSetPoint, avgIrr, powerSetPoints, invPower, plantPower, timeStamp);

    }
}
