public class Main {

    public static void main(String[] args) {

        // Need to modify inverter class to account for possibility of adding deadtime or other local inverter effects

        Simulator sim = new Simulator();

        for (int i = 0; i < 20; i++) {
            System.out.println("Step " + i);
            sim.simStep();
        }

        System.out.println("Simulation complete");


    }

}
