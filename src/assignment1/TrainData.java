import java.util.ArrayList;

public class TrainData {

    private ArrayList<TrainTarget> trainTargets;

    public TrainData(ArrayList<TrainTarget> trainTargets) {
        this.trainTargets = trainTargets;
    }

    public ArrayList<TrainTarget> getTrainTargets() {
        return trainTargets;
    }
}
