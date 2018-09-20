import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class that trains a neural network
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Trainer {
    private final int kFold;
    private NeuralNetwork neuralNetwork;
    private int currentDataIndex;
    private int currentEpoch;
    private ArrayList<TrainData> trainData;
    private ArrayList<TrainTarget> trainingSet;
    private ArrayList<NetworkResult> networkResults;
    private int currentMSE = Integer.MAX_VALUE;

    /**
     * Constructor for the trainer object.
     *
     * @param neuralNetwork the neural network that will be trained
     * @param trainingSet   the whole training set.
     * @param kFold         the number of partitions used for training / validation.
     */
    public Trainer(NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainingSet, int kFold) {
        this.neuralNetwork = neuralNetwork;
        this.currentEpoch = 1;
        this.currentDataIndex = 0;
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
        ArrayList<TrainTarget> trainingSet = new ArrayList<>();

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
                trainingSet.add(new TrainTarget(features, desiredOutputs));
            }
            //Randomly shuffle the trainData list.
            Collections.shuffle(trainingSet);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return trainingSet;
    }

    public static ArrayList<TrainData> kFold(int k, ArrayList<TrainTarget> trainingSet) {
        ArrayList<TrainData> trainingSets = new ArrayList<TrainData>();
        int trainingSetSize = trainingSet.size();
        int foldSize = trainingSetSize / k;
        System.out.println("TrainingSetSize: " + trainingSetSize);
        System.out.println("Partitioning trainingSet in " + k + " partitions with a size of " + foldSize);
        for (int i = 0; i < k; i++) {
            ArrayList<TrainTarget> targets = new ArrayList<TrainTarget>();
            for (int j = i * foldSize; j < (i + 1) * foldSize; j++) {
                targets.add(trainingSet.get(j));
            }
            trainingSets.add(new TrainData(targets));
        }
        return trainingSets;
    }

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


    public void trainKFoldNetwork() {
        for (int validation = 0; validation < kFold; validation++) {
            int test = validation + 1;
            if (test >= kFold) {
                test = 0;
            }
            double lastMSE = Double.MAX_VALUE;
            double currentMSE = Double.MIN_VALUE;
            //Check whether the error is increasing, if that is the case minimum has been reached.
            while (currentMSE <= lastMSE) {
                //compute error with the validation set
                lastMSE = computeMSE(validation);

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

            }

            //finished training with validation set, now check number of incorrect classifications with test set.
            float[] checkValidation = checkKFoldNetwork(validation);
            float[] checkTest = checkKFoldNetwork(test);
            System.out.println("At t=" + test + " v=" + validation + " Validation Errors: " + checkValidation[0] + " / " + checkValidation[1] + "%");
            this.networkResults.add(new NetworkResult(this.neuralNetwork, checkValidation, checkTest));
            System.out.println("Test Errors: " + checkTest[0] + " / " + checkTest[1] + "%");
            resetNeuralNetwork();
            System.out.println("");
        }
    }

    public double computeMSE(int validation) {
        double mse = 0;
        for (TrainTarget tt : trainData.get(validation).getTrainTargets()) {
            double[] inputs = tt.getInputs();
            double[] desiredOutputs = tt.getDesiredOutputs();
            mse += neuralNetwork.calculateMSE(inputs, desiredOutputs);
        }
        return mse;
    }

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


    public static double round(double number) {
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    /**
     * Finds the best parameters for a given amount of epochs using multi threading
     *
     * @param parameters
     * @param amountOfEpochs
     * @param amountOfThreads
     * @throws Exception
     */
    public void findBestParametersMultiThreaded(TrainParameters parameters, int amountOfEpochs, int amountOfThreads) throws Exception {
        ArrayList<Future> results = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(amountOfThreads);

        for (int i = 0; i < amountOfThreads; i++) {
            results.add(executorService.submit(new FindBestParameterThread(parameters, neuralNetwork, amountOfEpochs, 6)));
        }
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
        double amountOfTasksDouble = ((maxLearningRate - minLearningRate) / stepSizeLearningRate);
        int amountOfTasks = 0;

        amountOfTasks = Math.round(amountOfTasks);

        final int itemsPerThread = (amountOfTasks / (amountOfThreads));
        final int remainingItems = (amountOfTasks % (amountOfThreads));

        double[][] tasks = new double[amountOfThreads][2];

        double currentNumber = minLearningRate;

        for (int i = 0; i < tasks.length - 1; i++) {
            tasks[i][0] = currentNumber;
            tasks[i][1] = currentNumber + itemsPerThread * stepSizeLearningRate;
            currentNumber += stepSizeLearningRate;
        }

        tasks[tasks.length - 1][0] = currentNumber;
        tasks[tasks.length - 1][1] = maxLearningRate;

        return tasks;

    }

    /**
     * Finds the best parameters for a given amount of epochs
     *
     * @param parameters
     * @param amountOfEpochs
     * @return
     */
    public ArrayList<TrainResult> findBestParameters(TrainParameters parameters, int amountOfEpochs) {
        NeuralNetwork currentNetwork = neuralNetwork;

        double currentMSE;

        double lowestMSE = Double.MAX_VALUE;
        double lowestMSELearningRate = 0;
        int lowestMSEAmountOfNeurons = 0;

        ArrayList<TrainResult> results = new ArrayList<>();

        long startTime = System.currentTimeMillis();
        for (double lr = parameters.getMinLearningRate(); lr <= parameters.getMaxLearningRate(); lr += parameters.getStepSizeLearningRate()) {
            System.out.println("next learning rate");
            for (int hn = parameters.getMinAmountOfHiddenNeurons(); hn <= parameters.getMaxAmountOfHiddenNeurons(); hn++) {
                System.out.println("next hidden neuron");
                for (double minw = parameters.getMinInitialWeightInterval()[0]; minw <= parameters.getMinInitialWeightInterval()[1]; minw += parameters.getStepSizeWeight()) {
                    System.out.println("next min initial weight");
                    for (double maxw = parameters.getMaxInitialWeightInterval()[0]; maxw <= parameters.getMaxInitialWeightInterval()[1]; maxw += parameters.getStepSizeWeight()) {
                        System.out.println("next max initial weight");
                        for (double mint = parameters.getMinInitialTresholdInterval()[0]; mint <= parameters.getMinInitialTresholdInterval()[1]; mint += parameters.getStepSizeWeight()) {
                            for (double maxt = parameters.getMaxInitialTreshldInterval()[0]; maxt <= parameters.getMaxInitialTreshldInterval()[1]; maxt += parameters.getStepSizeWeight()) {
                                neuralNetwork = new NeuralNetwork(currentNetwork.getInputLayer().size(), hn, currentNetwork.getOutputLayer().size(), lr, minw, maxw, mint, maxt);
                                //TODO: Implement the k-fold method.
                                this.trainNetwork(amountOfEpochs);
                                currentMSE = this.computeMSE(1);
                                if (currentMSE < lowestMSE) {
                                    lowestMSE = currentMSE;
                                    lowestMSELearningRate = lr;
                                    lowestMSEAmountOfNeurons = hn;
                                }

                                TrainResult result = new TrainResult(hn, lr, minw, maxw, mint, maxt, amountOfEpochs, currentMSE, 0);

                                results.add(result);
                                //System.out.println(result);

                            }
                        }
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("taken time: " + (endTime - startTime));

        return results;
    }
}
