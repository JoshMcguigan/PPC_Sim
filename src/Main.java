public class Main {

    public static void main(String[] args) {

        System.out.println("Simulation starting");

        Simulator sim = new Simulator();
        
        for (int i = 0; i < 20; i++) {
            System.out.println("Step " + i);
            sim.simStep();
        }

        System.out.println("Simulation complete");

    }

}
