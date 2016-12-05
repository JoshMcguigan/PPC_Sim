package ppcSim.sim;

class AbstractSimulationObject {
    protected double lastSimTime; // Stores the timestamp of the last simulation execution

    AbstractSimulationObject(){
        lastSimTime = 0;
    }

    protected double getTimeDeltaAndUpdateTimeStamp(double currentTimeStamp){
        double timeDelta = currentTimeStamp - lastSimTime;
        lastSimTime = currentTimeStamp;
        return timeDelta;
    }
}
