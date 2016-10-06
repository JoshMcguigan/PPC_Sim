import java.util.Arrays;

/**
 * Created by Josh on 10/5/16.
 */
public class Simulator {

    private final double plantPowerSetPoint = 20.0;
    private final int invQuantity = 20;
    private final double maxIrr = 1500;
    private final double invMaxPower = 2.2;
    private final double invMaxIrr = 1400;
    private final double invVariability = 1;
    private double[] irradiance = new double[invQuantity];
    private double[] powerSetPoints = new double[invQuantity];
    private double[] invPower = new double[invQuantity];

    private AbstractSun sun = new SimpleRandomSun(maxIrr, invQuantity);
    private Inverter[] inverter = new Inverter[invQuantity];
    private AbstractController controller = new OpenLoopController(invQuantity, invMaxPower);
    private Substation substation = new Substation();

    Simulator(){

        // Instantiate the inverter array with identical inverters
        Arrays.fill(inverter, new Inverter(invMaxPower, invMaxIrr, invVariability));

    }


    public void simStep(){
        irradiance = sun.getIrradiance();
        double plantPower = substation.getPlantPower(invPower);
        powerSetPoints = controller.getPowerSetPoints(plantPowerSetPoint, plantPower, invPower);

        for (int i = 0; i < invQuantity; i++) {

            invPower[i] = inverter[i].getPower(powerSetPoints[i], irradiance[i]);

        }

        System.out.println("Plant Power: " + plantPower + " MW");

    }
}
