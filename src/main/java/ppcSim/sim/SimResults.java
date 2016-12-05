package ppcSim.sim;

import java.util.ArrayList;
import java.util.List;

public class SimResults {
    private List<List<PlantDataInstant>> simResults;
    private String[] controllerNames;

    SimResults(String[] controllerNames){

        this.controllerNames = controllerNames;

        simResults = new ArrayList<List<PlantDataInstant>>();

        for (int i = 0; i < controllerNames.length; i++) {
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

    public String[] getControllerNames(){
        return controllerNames;
    }

    void putPlantDataInstant(int controller, PlantDataInstant plantDataInstant){
        simResults.get(controller).add(plantDataInstant);
    }


}
