import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Created by Josh on 10/5/16.
 */
public class ControllerSimulator {

    private final double plantPowerSetPoint = 15.0;
    private final int invQuantity;
    private final double maxIrr;
    private final double invMaxIrr = 1400;
    private final double invVariability = 1;
    private double[] irradiance;
    private double[] powerSetPoints;
    private double[] invPower;

    private AbstractSun sun;
    private Inverter[] inverter;
    private AbstractController controller;
    private Substation substation;

    ControllerSimulator(int invQuantity, double maxIrr, double invMaxPower, AbstractController controller){

        this.invQuantity = invQuantity;
        this.maxIrr = maxIrr;
        this.controller = controller;

        irradiance = new double[invQuantity];
        powerSetPoints = new double[invQuantity];
        invPower = new double[invQuantity];

        sun = new SimpleRandomSun(maxIrr, invQuantity);
        inverter = new Inverter[invQuantity];
        substation = new Substation();

        // Instantiate the inverter array with identical inverters
        Arrays.fill(inverter, new Inverter(invMaxPower, invMaxIrr, invVariability));

    }

    public PlantData[] simRun(double[][] irradiance){

        int steps = irradiance.length;

        PlantData[] plantData = new PlantData[steps];

        for (int i = 0; i < steps; i++) {
            plantData[i] = simStep(irradiance[i]);
        }

        return plantData;
    }


    private PlantData simStep(double[] irradiance){

        double avgIrr = DoubleStream.of(irradiance).sum() / irradiance.length;
        double plantPower = substation.getPlantPower(invPower);
        powerSetPoints = controller.getPowerSetPoints(plantPowerSetPoint, plantPower, invPower);

        for (int i = 0; i < invQuantity; i++) {

            invPower[i] = inverter[i].getPower(powerSetPoints[i], irradiance[i]);

        }

        PlantData plantData = new PlantData(plantPowerSetPoint, avgIrr, powerSetPoints, invPower, plantPower);

        return plantData;

    }
}
