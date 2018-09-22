import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class that trains a neural network
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Trainer {
    private NeuralNetwork neuralNetwork;
    private final int kFold;
    private int currentDataIndex;
    private int currentEpoch;
    private ArrayList<TrainData> trainData;
    private ArrayList<TrainTarget> trainingSet;
    private ArrayList<NetworkResult> networkResults;
    private double currentMSE;


    /**
     * Constructor for the Trainer object.
     *
     * @param neuralNetwork
     * @param trainingSet
     * @param kFold
     */
    public Trainer(NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainingSet, int kFold) {
        this.neuralNetwork = neuralNetwork;
        currentEpoch = 1;
        currentDataIndex = 0;
        currentMSE = Integer.MAX_VALUE;
        this.trainingSet = trainingSet;
        this.trainData = kFold(kFold, trainingSet);
        this.kFold = kFold;
        this.networkResults = new ArrayList<NetworkResult>();
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    /**
     * Creates a list of features and targets from two files and combines them into a list of products.
     *
     * @param featuresUrl the url for the location of the features file
     * @param targetsUrl  the url for the location of the targets file
     * @return list of generated products
     */
    public static ArrayList<TrainTarget> loadData(String featuresUrl, String targetsUrl) {
        ArrayList<TrainTarget> trainData = new ArrayList<>();

        try {
            // Create the readers for the features and targets files
            FileReader featuresFileReader = new FileReader(featuresUrl);
            FileReader targetsFileReader = new FileReader(targetsUrl);

            BufferedReader featuresReader = new BufferedReader(featuresFileReader);
            BufferedReader targetsReader = new BufferedReader(targetsFileReader);

            while (featuresReader.ready()) {
                // Get the current line of 10 features and convert them to an array of doubles
                String currentFeatures = featuresReader.readLine();
                String[] featureStrings = currentFeatures.split(",");
                double[] features = new double[featureStrings.length];

                for (int i = 0; i < features.length; i++) {
                    features[i] = Double.parseDouble(featureStrings[i]);
                }

                // Get the current target and convert it to an int
                int target = Integer.parseInt(targetsReader.readLine());

                double[] desiredOutputs = new double[7];

                for (int i = 0; i < desiredOutputs.length; i++) {
                    if (target == (i + 1)) {
                        desiredOutputs[i] = 1.0;
                    } else {
                        desiredOutputs[i] = 0.0;
                    }
                }

                // Create a product with the current features and targets and add it to the list
                trainData.add(new TrainTarget(features, desiredOutputs));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return trainData;
    }

    public static ArrayList<TrainData> kFold(int k, ArrayList<TrainTarget> trainingSet) {
        ArrayList<TrainData> trainingSets = new ArrayList<TrainData>();
        int trainingSetSize = trainingSet.size();
        int foldSize = trainingSetSize / k;
//        System.out.println("TrainingSetSize: " + trainingSetSize);
//        System.out.println("Partitioning trainingSet in " + k + " partitions with a size of " + foldSize);
        for (int i = 0; i < k; i++) {
            ArrayList<TrainTarget> targets = new ArrayList<TrainTarget>();
            for (int j = i * foldSize; j < (i + 1) * foldSize; j++) {
                targets.add(trainingSet.get(j));
            }
            trainingSets.add(new TrainData(targets));
        }
        return trainingSets;
    }


    /**
     * Rounds a number to 4 decimal places.
     *
     * @param number the number to be rounded
     * @return
     */
    public static double round(double number) {
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * Train the current neural network.
     */
    public void trainNetwork(int epochs) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (currentDataIndex = 0; currentDataIndex < trainData.size(); currentDataIndex++) {
                double[] inputs = trainingSet.get(currentDataIndex).getInputs();
                double[] desiredOutputs = trainingSet.get(currentDataIndex).getDesiredOutputs();
                neuralNetwork.trainNetwork(inputs, desiredOutputs);
            }
            currentEpoch++;
            currentDataIndex = 0;
        }

    }

    public int checkNetwork() {
        int incorrectClassifications = 0;
        for (currentDataIndex = 0; currentDataIndex < trainingSet.size(); currentDataIndex++) {
            double[] inputs = trainingSet.get(currentDataIndex).getInputs();
            double[] desiredOutputs = trainingSet.get(currentDataIndex).getDesiredOutputs();
            double[] currentOutputs = neuralNetwork.computeOutput(inputs);
            int currentTarget = getTarget(currentOutputs);
            int desiredTarget = getTarget(desiredOutputs);
            if (currentTarget != desiredTarget) {
                incorrectClassifications++;
            }
        }
        return incorrectClassifications;
    }


    public int trainKFoldNetwork() {
        int amountOfEpochs = 1;
        int test = 0;
        int validation = 1;
        double lastMSE = Double.MAX_VALUE;
        double currentMSE = Double.MIN_VALUE;

        amountOfEpochs = 1;

        NeuralNetwork tempNetwork = neuralNetwork;

        //Check whether the error is increasing, if that is the case minimum has been reached.
        while (true) {
            //compute error with the validation set
            lastMSE = computeMSE(validation);
            tempNetwork = neuralNetwork;

            //Train with the trainingSet
            for (int i = 0; i < trainData.size(); i++) {
                if (i == validation || i == test)
                    continue;

                for (TrainTarget tt : trainData.get(i).getTrainTargets()) {
                    double[] inputs = tt.getInputs();
                    double[] desiredOutputs = tt.getDesiredOutputs();
                    neuralNetwork.trainNetwork(inputs, desiredOutputs);
                }
            }
            //compute error with the validation set
            currentMSE = computeMSE(validation);
            amountOfEpochs++;
            if(currentMSE >= lastMSE)
                break;
        }
        neuralNetwork = tempNetwork;
        //finished training with validation set, now check number of incorrect classifications with test set.
        float[] checkValidation = checkKFoldNetwork(validation);
        float[] checkTest = checkKFoldNetwork(test);
//        System.out.println("Epochs=" + amountOfEpochs + " -V- MSE=" + computeMSE(validation) + " E=" + checkValidation[0] + " P=" + checkValidation[1] + "%" + " -T- MSE=" + computeMSE(test) + " E=" + checkTest[0] + " P=" + checkTest[1] + "%");
        this.networkResults.add(new NetworkResult(this.neuralNetwork, checkValidation, checkTest));

        return amountOfEpochs;
    }

    /**
     * Compute the average MSE of a certain training data partition
     *
     * @param validation partition index
     * @return average MSE
     */
    public double computeMSE(int validation) {
        double mse = 0;
        for (TrainTarget tt : trainData.get(validation).getTrainTargets()) {
            double[] inputs = tt.getInputs();
            double[] desiredOutputs = tt.getDesiredOutputs();
            mse += neuralNetwork.calculateMSE(inputs, desiredOutputs);
        }
        return mse / trainData.get(validation).getTrainTargets().size();
    }

    /**
     * Check how well the network performs in a specific training data partition, preferably one that wasn't included while training.
     *
     * @param test partition index
     * @return float[0] is the number of incorrect classifications
     * float[1] is the percentage
     */
    public float[] checkKFoldNetwork(int test) {
        float incorrectClassifications = 0;
        ArrayList<TrainTarget> targets = trainData.get(test).getTrainTargets();
        for (TrainTarget tt : targets) {
            double[] inputs = tt.getInputs();
            double[] desiredOutputs = tt.getDesiredOutputs();
            double[] currentOutputs = neuralNetwork.computeOutput(inputs);
            int currentTarget = getTarget(currentOutputs);
            int desiredTarget = getTarget(desiredOutputs);
            if (currentTarget != desiredTarget) {
                incorrectClassifications++;
            }
        }
        float percentage = incorrectClassifications / targets.size() * 100.0f;
        return new float[]{incorrectClassifications, percentage};
    }

    /**
     * Reset the neuralNetwork in the trainer.
     */
    private void resetNeuralNetwork() {
        int inputSize = this.neuralNetwork.getInputLayerSize();
        int hiddenSize = this.neuralNetwork.getHiddenLayerSize();
        int outputSize = this.neuralNetwork.getOutputLayerSize();
        double learningRate = this.neuralNetwork.getLearningRate();
        double minInitialWeight = this.neuralNetwork.getMinInitialWeight();
        double maxInitialWeight = this.neuralNetwork.getMaxInitialWeight();
        double minInitialTreshold = this.neuralNetwork.getMinInitialTreshold();
        double maxInitialTreshold = this.neuralNetwork.getMaxInitialTreshold();

        this.neuralNetwork = new NeuralNetwork(inputSize, hiddenSize, outputSize, learningRate, minInitialWeight, maxInitialWeight, minInitialTreshold, maxInitialTreshold);
    }


    public int getTarget(double[] outputs) {
        double currentMax = Double.MIN_VALUE;
        int currentTarget = 0;

        for (int i = 0; i < outputs.length; i++) {
            if (outputs[i] > currentMax) {
                currentMax = outputs[i];
                currentTarget = i + 1;
            }
        }
        return currentTarget;

    }

    public String outputToString(double[] inputs) {
        String result = "";

        for (double input : inputs) {
            result += String.valueOf(input) + " , ";
        }

        return result;
    }

    public ArrayList<NetworkResult> getNetworkResults() {
        return networkResults;
    }


    /**
     * Finds the best parameters for a given amount of epochs using multi threading
     *
     * @param parameters      the parameters that will be tested
     * @param amountOfThreads the amount of threads that will be used to calculate the results
     */
    public ArrayList<TrainResult> findBestParametersMultiThreaded(TrainParameters parameters, int amountOfThreads) {
        ArrayList<Future> resultFutures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(amountOfThreads);

        double[][] learningRateIntervals = divideLearningRateTasks(amountOfThreads, parameters.getMinLearningRate(), parameters.getMaxLearningRate(), parameters.getStepSizeLearningRate());

        for (int i = 0; i < amountOfThreads; i++) {
            System.out.println(learningRateIntervals[i][0]);
            System.out.println(learningRateIntervals[i][1]);
            resultFutures.add(executorService.submit(new FindBestParameterThread(
                    new TrainParameters(parameters.getMinAmountOfHiddenNeurons(),
                            parameters.getMaxAmountOfHiddenNeurons(),
                            learningRateIntervals[i][0],
                            learningRateIntervals[i][1],
                            parameters.getStepSizeLearningRate(),
                            parameters.getMinInitialWeightInterval(),
                            parameters.getMaxInitialWeightInterval(),
                            parameters.getMinInitialTresholdInterval(),
                            parameters.getMaxInitialTreshldInterval(),
                            parameters.getStepSizeWeight()), neuralNetwork, loadData("src/files/features.txt", "src/files/targets.txt"), i, kFold)));
        }

        boolean finished = false;

        while (!finished) {
            finished = true;
            for (int i = 0; i < resultFutures.size(); i++) {
                if (!resultFutures.get(i).isDone()) {
                    finished = false;
                }
            }
        }

        System.out.println("finished");

        ArrayList<TrainResult> results = new ArrayList<>();

        for (int i = 0; i < resultFutures.size(); i++) {
            ArrayList<TrainResult> currentResult = null;
            try {
                if (resultFutures.get(i).isDone()) {
                    currentResult = (ArrayList<TrainResult>) resultFutures.get(i).get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            results.addAll(currentResult);
        }

        return results;
    }

    /**
     * Method that divides the learning rates over the given amount of threads
     *
     * @param amountOfThreads
     * @param minLearningRate
     * @param maxLearningRate
     * @param stepSizeLearningRate
     * @return
     * @throws Exception
     */
    public static double[][] divideLearningRateTasks(int amountOfThreads, double minLearningRate, double maxLearningRate, double stepSizeLearningRate) {

        int amountOfTasks = (int) Math.round(((maxLearningRate - minLearningRate + stepSizeLearningRate) / stepSizeLearningRate));

        int itemsPerThread = amountOfTasks / amountOfThreads;
        int remainingItems = (amountOfTasks % (amountOfThreads));


        double[][] tasks = new double[amountOfThreads][2];

        double currentNumber = minLearningRate;

        for (int i = 0; i < remainingItems; i++) {
            tasks[i][0] = currentNumber;
            tasks[i][1] = currentNumber + (itemsPerThread) * stepSizeLearningRate;
            currentNumber += (itemsPerThread + 1) * stepSizeLearningRate;
            currentNumber = Math.round(currentNumber * 10) / 10.0;
        }

        for (int i = remainingItems; i < tasks.length - 1; i++) {
            tasks[i][0] = currentNumber;
            tasks[i][1] = currentNumber + (itemsPerThread - 1) * stepSizeLearningRate;
            currentNumber += itemsPerThread * stepSizeLearningRate;
            currentNumber = Math.round(currentNumber * 10) / 10.0;
        }

        tasks[tasks.length - 1][0] = currentNumber;
        tasks[tasks.length - 1][1] = maxLearningRate;

        return tasks;
    }

    /**
     * Finds the best parameters for a given amount of epochs
     *
     * @param parameters the parameters that will be tested
     * @return list of results with the given parameters
     */
    public ArrayList<TrainResult> findBestParameters(TrainParameters parameters) {
        NeuralNetwork currentNetwork = neuralNetwork;

        System.out.println("finding best parameters for: " + parameters.getMinLearningRate() + " to " + parameters.getMaxLearningRate());

        double currentMSE;

        double lowestMSE = Double.MAX_VALUE;
        TrainResult lowestMSEResult = null;

        ArrayList<TrainResult> results = new ArrayList<>();

        for (double lr = parameters.getMinLearningRate(); lr <= parameters.getMaxLearningRate(); lr += Math.round(parameters.getStepSizeLearningRate() * 10.0) / 10.0) {
            for (int hn = parameters.getMinAmountOfHiddenNeurons(); hn <= parameters.getMaxAmountOfHiddenNeurons(); hn++) {
                for (double minw = parameters.getMinInitialWeightInterval()[0]; minw <= parameters.getMinInitialWeightInterval()[1]; minw += parameters.getStepSizeWeight()) {
                    for (double maxw = parameters.getMaxInitialWeightInterval()[0]; maxw <= parameters.getMaxInitialWeightInterval()[1]; maxw += parameters.getStepSizeWeight()) {
                        if (maxw < minw) {
                            continue;
                        }

                        for (double mint = parameters.getMinInitialTresholdInterval()[0]; mint <= parameters.getMinInitialTresholdInterval()[1]; mint += parameters.getStepSizeWeight()) {
                            for (double maxt = parameters.getMaxInitialTreshldInterval()[0]; maxt <= parameters.getMaxInitialTreshldInterval()[1]; maxt += parameters.getStepSizeWeight()) {
                                neuralNetwork = new NeuralNetwork(currentNetwork.getInputLayer().size(), hn, currentNetwork.getOutputLayer().size(), lr, minw, maxw, mint, maxt);
                                int amountOfEpochs = this.trainKFoldNetwork();
                                currentMSE = this.computeMSE(1);

                                TrainResult result = new TrainResult(hn, lr, minw, maxw, mint, maxt, amountOfEpochs, currentMSE, 0);

                                if (currentMSE < lowestMSE) {
                                    lowestMSE = currentMSE;
                                    lowestMSEResult = result;
                                }

                                results.add(result);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("best result: " + lowestMSEResult.toString());

        return results;
    }

    /**
     * Create a file containing all the results from the given set of testing parameters.
     *
     * @param parameters      the parameters that will be tested
     * @param amountOfThreads the amount of threads that will be created to calculate the results
     */
    public void createParameterFile(TrainParameters parameters, int amountOfThreads) {
        File file = new File("test.txt");

        try {
            PrintWriter printWriter = new PrintWriter(file);

            printWriter.println("lr, " + "hn, " + "minw, " + "maxw, " + "mint, " + "maxt, " + "ep, " + "mse, " + "err");

            long startTime = System.currentTimeMillis();

            ArrayList<TrainResult> results = findBestParametersMultiThreaded(parameters, amountOfThreads);

            long endTime = System.currentTimeMillis();
            System.out.println("taken time: " + (endTime - startTime));

            for (TrainResult result : results) {
                printWriter.println(result.toString());
            }
            printWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
