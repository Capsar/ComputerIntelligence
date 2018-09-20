import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Sam van Berkel on 19/09/2018.
 */
public class FindBestParameterThread implements Callable<ArrayList<TrainResult>> {
    private TrainParameters parameters;
    private NeuralNetwork neuralNetwork;
    int amountOfEpochs;

    public FindBestParameterThread(TrainParameters parameters, NeuralNetwork neuralNetwork, int amountOfEpochs) {
        this.parameters = parameters;
        this.neuralNetwork = neuralNetwork;
        this.amountOfEpochs = amountOfEpochs;
    }

    @Override
    public ArrayList<TrainResult> call() throws Exception {
        Trainer trainer = new Trainer(neuralNetwork, Trainer.loadData("files/features.txt", "files/targets.txt"), 0.1);

        return trainer.findBestParameters(parameters, amountOfEpochs);
    }
}
