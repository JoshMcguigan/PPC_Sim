package ppcSim.sim;

public enum SunFactory {
    TRIANGLEWAVE("Triangle Wave Sun"),
    SQUAREWAVE("Square Wave Sun");

    private SunFactory(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }

    public AbstractSun get(SimulatorSettings simulatorSettings){
        switch(this){
            case TRIANGLEWAVE: return new TriangleWaveSun(simulatorSettings.maxIrr, simulatorSettings.invQuantity);
            case SQUAREWAVE: return new SquareWaveSun(simulatorSettings.maxIrr, simulatorSettings.invQuantity);
            default: throw new UnsupportedOperationException();
        }
    }

    private String name;
}
