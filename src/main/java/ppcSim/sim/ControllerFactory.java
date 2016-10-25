package ppcSim.sim;

public enum ControllerFactory {
    NAIVE("Naive Controller"),
    OPENLOOP("Open Loop Controller"),
    PROPORTIONALSTEP("Proportional Step Controller"),
    COMPLEX("Complex Controller");

    private ControllerFactory(String controllerName){
        this.controllerName = controllerName;
    }

    public String toString(){
        return controllerName;
    }

    public AbstractController get(SimulatorSettings simulatorSettings){
        switch(this){
            case NAIVE: return new NaiveController(simulatorSettings.invQuantity, simulatorSettings.invMaxPower);
            case OPENLOOP: return new OpenLoopController(simulatorSettings.invQuantity, simulatorSettings.invMaxPower);
            case PROPORTIONALSTEP: return new ProportionalStepController(simulatorSettings.invQuantity, simulatorSettings.invMaxPower, simulatorSettings.controllerExecutionRate);
            case COMPLEX: return new ComplexController(simulatorSettings.invQuantity, simulatorSettings.invMaxPower, simulatorSettings.controllerExecutionRate);
            default: throw new UnsupportedOperationException();
        }
    }

    private String controllerName;
}
