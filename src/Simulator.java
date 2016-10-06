/**
 * Created by Josh on 10/5/16.
 */
public class Simulator {
    int invQuantity = 20;
    double maxIrr = 1500;
    double[] irradiance = new double[invQuantity];
    double[] powerSetPoints = new double[invQuantity];
    double[] invPower = new double[invQuantity];

    Sun sun = new Sun(maxIrr, invQuantity);
    Controller controller = new Controller(invQuantity);
    Substation substation = new Substation();

    Simulator(){

    }


    public void simStep(){
        irradiance = sun.getIrradiance();
        powerSetPoints = controller.getPowerSetPoints();

        for (int i = 0; i < invQuantity; i++) {

            invPower[i] = Inverter.getPower(powerSetPoints[i], irradiance[i]);

        }

        System.out.println("Plant Power: " + substation.getPlantPower(invPower) + " MW");

    }
}
