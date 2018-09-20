import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Sam van Berkel on 19/09/2018.
 */
public class FindBestParameterThread implements Callable<ArrayList<TrainResult>> {
    private TrainParameters parameters;
    private NeuralNetwork neuralNetwork;
    private ArrayList<TrainTarget> trainData;
    private int amountOfEpochs;
    private int threadId;
    private double minLearnRate;
    private double maxLearnRate;

    public FindBestParameterThread(TrainParameters parameters, double minLearnRate, double maxLearnRate, NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainData, int amountOfEpochs, int threadId) {
        this.parameters = parameters;
        this.neuralNetwork = neuralNetwork;
        this.trainData = trainData;
        this.amountOfEpochs = amountOfEpochs;
        this.threadId = threadId;
        this.minLearnRate = minLearnRate;
        this.maxLearnRate = maxLearnRate;
    }

    @Override
    public ArrayList<TrainResult> call() throws Exception {
        parameters.setMinLearningRate(minLearnRate);
        parameters.setMaxLearningRate(maxLearnRate);

        System.out.println("Starting thread " + threadId + " with learning rates from: " + parameters.getMinLearningRate() + " to: " + parameters.getMaxLearningRate());



        Trainer trainer = new Trainer(neuralNetwork, trainData, 0.1);

        long startTime = System.currentTimeMillis();

        ArrayList<TrainResult> result = trainer.findBestParameters(parameters, amountOfEpochs);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Thread " + threadId + "has finished in " + (totalTime / 1000) + " seconds");

        return result;
    }
}
