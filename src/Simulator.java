import java.util.Arrays;

/**
 * Created by Josh on 10/5/16.
 */
public class Simulator {
    int invQuantity = 20;
    double maxIrr = 1500;
    double invMaxPower = 2.2;
    double invMaxIrr = 1400;
    double invVariability = 1;
    double[] irradiance = new double[invQuantity];
    double[] powerSetPoints = new double[invQuantity];
    double[] invPower = new double[invQuantity];

    Sun sun = new Sun(maxIrr, invQuantity);
    Inverter[] inverter = new Inverter[invQuantity];
    Controller controller = new Controller(invQuantity);
    Substation substation = new Substation();

    Simulator(){

        // Instantiate the inverter array with identical inverters
        Arrays.fill(inverter, new Inverter(invMaxPower, invMaxIrr, invVariability));

    }


    public void simStep(){
        irradiance = sun.getIrradiance();
        powerSetPoints = controller.getPowerSetPoints();

        for (int i = 0; i < invQuantity; i++) {

            invPower[i] = inverter[i].getPower(powerSetPoints[i], irradiance[i]);

        }

        System.out.println("Plant Power: " + substation.getPlantPower(invPower) + " MW");

    }
}
