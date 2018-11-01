package assignment1;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that trains a neural network
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Trainer {
    private NeuralNetwork neuralNetwork;
    private ArrayList<TrainData> trainData;
    private ArrayList<TrainTarget> trainingSet;
    private ArrayList<NetworkResult> networkResults;


    /**
     * Constructor for the Trainer object.
     *
     * @param neuralNetwork
     * @param trainingSet
     * @param kFold
     */
    public Trainer(NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainingSet, int kFold) {
        this.neuralNetwork = neuralNetwork;
        this.trainingSet = trainingSet;
        this.trainData = kFold(kFold, trainingSet);
        this.networkResults = new ArrayList<>();
    }

    /**
     * Creates a list of features and targets from two files and combines them into a list of products.
     *
     * @param featuresPath the url for the location of the features file
     * @param targetsPath  the url for the location of the targets file
     * @return list of generated products
     */
    public static ArrayList<TrainTarget> loadTrainingSet(String featuresPath, String targetsPath) {
        ArrayList<TrainTarget> trainingSet = new ArrayList<>();

        try {
            URL featuresUrl = Trainer.class.getResource(featuresPath);
            URL targetsUrl = Trainer.class.getResource(targetsPath);

            // Create the readers for the features and targets files
            FileReader featuresFileReader = new FileReader(featuresUrl.getPath());
            FileReader targetsFileReader = new FileReader(targetsUrl.getPath());

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

            //Randomize the trainingSet
            Collections.shuffle(trainingSet);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return trainingSet;
    }

    public static ArrayList<double[]> loadInputData(String filePath) {
        ArrayList<double[]> inputData = new ArrayList<>();
        URL fileUrl = Trainer.class.getResource(filePath);

        File inputs = new File(fileUrl.getPath());
        try {
            Files.lines(inputs.toPath()).forEach(s -> {
                String[] featureStrings = s.split(",");
                double[] features = new double[featureStrings.length];

                for (int i = 0; i < features.length; i++) {
                    features[i] = Double.parseDouble(featureStrings[i]);
                }

                // Create a product with the current features and targets and add it to the list
                inputData.add(features);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputData;
    }

    public static ArrayList<TrainData> kFold(int k, ArrayList<TrainTarget> trainingSet) {
        ArrayList<TrainData> trainingSets = new ArrayList<TrainData>();
        int trainingSetSize = trainingSet.size();
        int foldSize = trainingSetSize / k;
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
     * Converts a list of outputs to a chosen class
     *
     * @param outputs
     * @return
     */
    public static int convertOutputsToClass(double[] outputs) {
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

    public int trainKFoldNetwork() {
        int amountOfEpochs = 0;
        int test = 0;
        int validation = 1;
        double lastMSE = Double.MAX_VALUE;
        double currentMSE = Double.MIN_VALUE;

        //Check whether the error is increasing, if that is the case minimum has been reached.
        while (currentMSE < lastMSE) {
            amountOfEpochs++;
            //compute error with the validation set
            lastMSE = computeMSE(validation);

            //Train with the trainingSet
            for (int i = 0; i < trainData.size(); i++) {
                //Skip the validation and test partition
                if (i == validation || i == test)
                    continue;

                //For each partition train the network with each data object
                for (TrainTarget tt : trainData.get(i).getTrainTargets()) {
                    double[] inputs = tt.getInputs();
                    double[] desiredOutputs = tt.getDesiredOutputs();
                    neuralNetwork.trainNetwork(inputs, desiredOutputs);
                }
            }
            //compute error with the validation set
            currentMSE = computeMSE(validation);
            if (amountOfEpochs > 1000) {
                System.out.println("Epochs over 1000");
                break;
            }

        }
        //finished training with validation set, now check number of incorrect classifications with test set.
        float[] checkValidation = checkKFoldNetwork(validation);
        float[] checkTest = checkKFoldNetwork(test);
        System.out.println("Epochs=" + amountOfEpochs + " -V- MSE=" + checkValidation[2] + " E=" + checkValidation[0] + " P=" + checkValidation[1] + "%" +
                                                        " -T- MSE=" + checkTest[2] + " E=" + checkTest[0] + " P=" + checkTest[1] + "%");
        this.networkResults.add(new NetworkResult(this.neuralNetwork, checkValidation, checkTest, this.trainingSet));

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
            int currentTarget = convertOutputsToClass(currentOutputs);
            int desiredTarget = convertOutputsToClass(desiredOutputs);
            if (currentTarget != desiredTarget) {
                incorrectClassifications++;
            }
        }
        float percentage = incorrectClassifications / targets.size() * 100.0f;
        return new float[]{incorrectClassifications, percentage, (float) computeMSE(test)};
    }

    public int[][] checkConfusionMatrix(int test) {
        int[][] result = new int[8][8];
        ArrayList<TrainTarget> targets = trainData.get(test).getTrainTargets();
        for (TrainTarget tt : targets) {
            double[] inputs = tt.getInputs();
            double[] desiredOutputs = tt.getDesiredOutputs();
            double[] currentOutputs = neuralNetwork.computeOutput(inputs);
            int currentTarget = convertOutputsToClass(currentOutputs);
            int desiredTarget = convertOutputsToClass(desiredOutputs);
            result[currentTarget][desiredTarget]++;
        }
        return result;
    }

    public ArrayList<NetworkResult> getNetworkResults() {
        return networkResults;
    }

    /**
     * Creates a file called classes.txt containing the matching classes for the given input file
     *
     * @param inputPath the url of the file containing the features
     */
    public void createOutputFile(String inputPath, String outputPath) {

        ArrayList<double[]> inputs = loadInputData(inputPath);
        System.out.println("Loaded " + inputs.size() + " inputs");
        try {
            PrintWriter printWriter = new PrintWriter(outputPath);
            for (double[] input : inputs) {
                int computedClass = convertOutputsToClass(neuralNetwork.computeOutput(input));
                printWriter.println(computedClass);
            }

            printWriter.close();
            System.out.println("Created file with classes");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
