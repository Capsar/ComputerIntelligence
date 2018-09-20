import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class that trains a neural network
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Trainer {
    NeuralNetwork neuralNetwork;
    int currentDataIndex;
    int currentEpoch;
    double currentMSE;
    double desiredMSE;
    ArrayList<TrainTarget> trainData;

    /**
     * Constructor for the trainer object.
     * @param neuralNetwork the neural network that will be trained
     * @param desiredMSE the desired mean squared error that needs to be reached before the network stops training
     */
    public Trainer(NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainData, double desiredMSE) {
        this.neuralNetwork = neuralNetwork;
        this.desiredMSE = desiredMSE;
        currentEpoch = 1;
        currentDataIndex = 0;
        currentMSE = Integer.MAX_VALUE;
        this.trainData = trainData;
    }

    public NeuralNetwork getNeuralNetwork() {
        return neuralNetwork;
    }

    /**
     * Creates a list of features and targets from two files and combines them into a list of products.
     * @param featuresUrl the url for the location of the features file
     * @param targetsUrl the url for the location of the targets file
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

            while(featuresReader.ready()) {
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

    /**
     * Rounds a number to 4 decimal places.
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
    public double trainNetwork(int maxAmountOfEpochs) {
        while (currentEpoch <= maxAmountOfEpochs) {
            currentMSE = neuralNetwork.trainNetwork(trainData.get(currentDataIndex).getInputs(), trainData.get(currentDataIndex).getDesiredOutputs());
            currentDataIndex++;

            if (currentDataIndex + 1 > trainData.size()) {

                currentEpoch++;
                currentDataIndex = 0;
            }
        }

        // Reset the values
        currentEpoch = 0;
        currentDataIndex = 0;

        return currentMSE;
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
        return  currentTarget;

    }

    public String outputToString(double[] inputs) {
        String result = "";

        for (double input : inputs) {
            result += String.valueOf(input) + " , ";
        }

        return result;
    }

    /**
     * Finds the best parameters for a given amount of epochs using multi threading
     * @param parameters
     * @param amountOfEpochs
     * @param amountOfThreads
     * @throws Exception
     */
    public void findBestParametersMultiThreaded(TrainParameters parameters, int amountOfEpochs, int amountOfThreads) throws Exception{


        ArrayList<Future> results = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(amountOfThreads);

        for (int i = 0; i < amountOfThreads; i++) {
            results.add(executorService.submit(new FindBestParameterThread(parameters, neuralNetwork, amountOfEpochs)));
        }
    }

    /**
     * Method that divides the learning rates over the given amount of threads
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
            currentNumber+= stepSizeLearningRate;
        }

        tasks[tasks.length - 1][0] = currentNumber;
        tasks[tasks.length - 1][1] = maxLearningRate;

        return tasks;

    }

    /**
     * Finds the best parameters for a given amount of epochs
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
        for (double lr = parameters.getMinLearningRate(); lr <= parameters.getMaxLearningRate(); lr+= parameters.getStepSizeLearningRate()) {
            System.out.println("next learning rate");
            for (int hn = parameters.getMinAmountOfHiddenNeurons(); hn <= parameters.getMaxAmountOfHiddenNeurons(); hn++) {
                System.out.println("next hidden neuron");
                for (double minw = parameters.getMinInitialWeightInterval()[0]; minw <= parameters.getMinInitialWeightInterval()[1]; minw+= parameters.getStepSizeWeight()) {
                    System.out.println("next min initial weight");
                    for (double maxw = parameters.getMaxInitialWeightInterval()[0]; maxw <= parameters.getMaxInitialWeightInterval()[1]; maxw+= parameters.getStepSizeWeight()) {
                        System.out.println("next max initial weight");
                        for (double mint = parameters.getMinInitialTresholdInterval()[0]; mint <= parameters.getMinInitialTresholdInterval()[1]; mint+= parameters.getStepSizeWeight()) {
                            for (double maxt = parameters.getMaxInitialTreshldInterval()[0]; maxt <= parameters.getMaxInitialTreshldInterval()[1]; maxt += parameters.getStepSizeWeight()) {
                                neuralNetwork = new NeuralNetwork(currentNetwork.getInputLayer().size(), hn, currentNetwork.getOutputLayer().size(), lr, minw, maxw, mint, maxt);
                                currentMSE = this.trainNetwork(amountOfEpochs);
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
        }}
        long endTime = System.currentTimeMillis();
        System.out.println("taken time: " + (endTime - startTime));

        return results;
    }
}
