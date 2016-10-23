package ppcSim.sim; /**
 * Created by Josh on 10/5/16.
 */

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.*;

public class Substation {

    private final double deadTime; // Time delay in reporting plant power
    private Map<Double,Double> dataStore; // Stores <timeStamp, calculated plant power>
    private double lastReportedPower; // Stores the last reported power value

    public Substation(double deadTime){
        this.deadTime = deadTime;
        dataStore = new HashMap<Double,Double>();
        lastReportedPower = 0;
    }

    public double getPlantPower(double[] invPower, double timeStamp){

        // Store the data
        dataStore.put(timeStamp, DoubleStream.of(invPower).sum());

        // Search for keys (timeStamps) which are older than current timeStamp-deadTime
        Double selectedTime = 0.0;
        Double sitePower = 0.0;
        for (Double key: dataStore.keySet() ){
            if ( timeStamp-deadTime >= key){
                // Choose the latest time stamp which is before the deadTime
                if (key >= selectedTime ) {
                    selectedTime = key;
                    sitePower = dataStore.get(selectedTime);
                }
            }
        }

        // Delete data which is older than the selected data
        Iterator<Map.Entry<Double,Double>> iter = dataStore.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Double,Double> entry = iter.next();
            if(entry.getKey() <= selectedTime){
                iter.remove();
            }
        }


        return sitePower;

    }
}
