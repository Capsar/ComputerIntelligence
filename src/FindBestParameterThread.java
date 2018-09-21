import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Thread that will calculate the best parameters for the neuralnetwork.
 * Created by Sam van Berkel on 19/09/2018.
 */
public class FindBestParameterThread implements Callable<ArrayList<TrainResult>> {
    private TrainParameters parameters;
    private NeuralNetwork neuralNetwork;
    private ArrayList<TrainTarget> trainData;
    private int threadId;
    private double minLearnRate;
    private double maxLearnRate;
    private int kFold;

    /**
     * Constructor for the FindBestParameterThread object
     * @param parameters parameters for the testing
     * @param neuralNetwork the neural network that will trained
     * @param trainData the data that will be used to train the network
     * @param threadId the id of the thread
     * @param kFold the amount of folds that will be used
     */
    public FindBestParameterThread(TrainParameters parameters, NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainData, int threadId, int kFold) {
        this.parameters = parameters;
        this.neuralNetwork = neuralNetwork;
        this.trainData = trainData;
        this.kFold = kFold;
        this.threadId = threadId;
    }

    @Override
    public ArrayList<TrainResult> call() throws Exception {

        System.out.println("Starting thread " + threadId + " with learning rates from: " + parameters.getMinLearningRate() + " to: " + parameters.getMaxLearningRate());



        Trainer trainer = new Trainer(neuralNetwork, trainData, 5);

        long startTime = System.currentTimeMillis();

        ArrayList<TrainResult> result = trainer.findBestParameters(parameters);

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Thread " + threadId + "has finished in " + (totalTime / 1000) + " seconds");

        return result;
    }
}
