/**
 * Created by Josh on 10/20/16.
 */
public class AbstractSimulationObject {
    protected double lastSimTime; // Stores the timestamp of the last simulation execution

    AbstractSimulationObject(){
        lastSimTime = 0;
    }

    protected double updateTimeStamp(double timeStamp){
        double timeDelta = timeStamp - lastSimTime;
        lastSimTime = timeStamp;
        return timeDelta;
    }
}
