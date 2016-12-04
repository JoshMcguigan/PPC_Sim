package ppcSim.sim;

import java.util.ArrayList;
import java.util.List;

public class SimResults {
    private List<List<PlantDataInstant>> simResults;

    public SimResults(int controllerQuantity){
        simResults = new ArrayList<List<PlantDataInstant>>();

        for (int i = 0; i < controllerQuantity; i++) {
            simResults.add(new ArrayList<PlantDataInstant>());
        }
    }

    public PlantDataInstant getPlantData(int controller, int step){
        return simResults.get(controller).get(step);
    }

    public PlantDataInstant[][] getPlantDataAsArray(){
        PlantDataInstant[][] array = simResults.stream()
                .map(l -> l.stream().toArray(PlantDataInstant[]::new))
                .toArray(PlantDataInstant[][]::new);
        return array;
    }

    void putPlantDataInstant(int controller, PlantDataInstant plantDataInstant){
        simResults.get(controller).add(plantDataInstant);
    }


}
