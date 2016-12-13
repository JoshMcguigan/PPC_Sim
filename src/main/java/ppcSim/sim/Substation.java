package ppcSim.sim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.*;

class Substation {

    private SubstationSettings settings;
    private Map<Double,Double> dataStore; // Stores <timeStamp, calculated plant power>

    private double sitePower;

    Substation(SubstationSettings settings){

        this.settings = settings;

        dataStore = new HashMap<Double,Double>();

        sitePower = 0;
    }

    double getPlantPower(double[] invPower, double timeStamp){

        // Store the data
        dataStore.put(timeStamp, DoubleStream.of(invPower).sum());

        // Search for keys (timeStamps) which are older than current timeStamp-deadTime
        Double selectedTime = 0.0;
        for (Double key: dataStore.keySet() ){
            if ( timeStamp - settings.deadTime >= key){
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
