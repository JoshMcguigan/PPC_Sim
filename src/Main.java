public class Main {

    public static void main(String[] args) {

        // Modify inverter class so it must be instantiated so time delay or other things could be added

        Simulator sim = new Simulator();

        for (int i = 0; i < 20; i++) {
            System.out.println("Step " + i);
            sim.simStep();
        }


    }

}
