import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Sam van Berkel on 19/09/2018.
 */
public class FindBestParameterThread implements Callable<ArrayList<TrainResult>> {
    private TrainParameters parameters;
    private NeuralNetwork neuralNetwork;
    int amountOfEpochs;
    private int kFold;

    public FindBestParameterThread(TrainParameters parameters, NeuralNetwork neuralNetwork, int amountOfEpochs, int kFold) {
        this.parameters = parameters;
        this.neuralNetwork = neuralNetwork;
        this.amountOfEpochs = amountOfEpochs;
        this.kFold = kFold;
    }

    @Override
    public ArrayList<TrainResult> call() throws Exception {
        Trainer trainer = new Trainer(neuralNetwork, Trainer.loadData("files/features.txt", "files/targets.txt"), kFold);

        return trainer.findBestParameters(parameters, amountOfEpochs);
    }
}
