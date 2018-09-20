import javax.sound.midi.SysexMessage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
    public ArrayList<TrainResult> findBestParametersMultiThreaded(TrainParameters parameters, int amountOfEpochs, int amountOfThreads) {
        ArrayList<Future> resultFutures = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(amountOfThreads);

        double[][] learningRateIntervals = divideLearningRateTasks(amountOfThreads, parameters.getMinLearningRate(), parameters.getMaxLearningRate(), parameters.getStepSizeLearningRate());

        for (int i = 0; i < amountOfThreads; i++) {
            System.out.println(learningRateIntervals[i][0]);
            System.out.println(learningRateIntervals[i][1]);
            resultFutures.add(executorService.submit(new FindBestParameterThread(parameters, learningRateIntervals[i][0] ,learningRateIntervals[i][1], neuralNetwork, trainData, amountOfEpochs, i)));
        }

        boolean finished = false;

        while(!finished) {
            finished = true;
            for (int i = 0; i < resultFutures.size(); i++) {
                if (!resultFutures.get(i).isDone()) {
                    finished = false;
                }
            }
        }

        System.out.println("finished");

        ArrayList<TrainResult> results = new ArrayList<>();



        try {
            ArrayList<TrainResult> results1 = (ArrayList<TrainResult>) resultFutures.get(0).get();
            ArrayList<TrainResult> results2 = (ArrayList<TrainResult>) resultFutures.get(1).get();
            ArrayList<TrainResult> results3 = (ArrayList<TrainResult>) resultFutures.get(2).get();
            ArrayList<TrainResult> results4 = (ArrayList<TrainResult>) resultFutures.get(3).get();
            ArrayList<TrainResult> results5 = (ArrayList<TrainResult>) resultFutures.get(4).get();
            ArrayList<TrainResult> results6 = (ArrayList<TrainResult>) resultFutures.get(5).get();
            ArrayList<TrainResult> results7 = (ArrayList<TrainResult>) resultFutures.get(6).get();
            ArrayList<TrainResult> results8 = (ArrayList<TrainResult>) resultFutures.get(7).get();

            System.out.println("results 1:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results1.get(i).getLearningRate());
            }

            System.out.println("results 2:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results2.get(i).getLearningRate());
            }

            System.out.println("results 3:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results3.get(i).getLearningRate());
            }

            System.out.println("results 4:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results4.get(i).getLearningRate());
            }

            System.out.println("results 5:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results5.get(i).getLearningRate());
            }

            System.out.println("results 6:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results6.get(i).getLearningRate());
            }

            System.out.println("results 7:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results7.get(i).getLearningRate());
            }

            System.out.println("results 8:");
            for (int i = 0; i < results1.size(); i++) {
                System.out.println(results8.get(i).getLearningRate());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < resultFutures.size(); i++) {
            ArrayList<TrainResult> currentResult = null;
            try {
                if (resultFutures.get(i).isDone()) {
                    currentResult = (ArrayList<TrainResult>) resultFutures.get(i).get();
                }

                //for (int j = 0; j < currentResult.size(); j++) {
                //    System.out.println("lr1: " + results.get(j).getLearningRate());
                //}
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            results.addAll(currentResult);
            //for (int j = 0; j < currentResult.size(); j++) {
            //    System.out.println("lr1: " + results.get(j).getLearningRate());
            //}
        }

        //for (int i = 0; i < results.size(); i++) {
        //    System.out.println("lr: " + results.get(i).getLearningRate());
        //}

        return results;
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

        int amountOfTasks = (int) Math.round(((maxLearningRate - minLearningRate + stepSizeLearningRate) / stepSizeLearningRate));

        int itemsPerThread = amountOfTasks / amountOfThreads;
        int remainingItems = (amountOfTasks % (amountOfThreads));


        double[][] tasks = new double[amountOfThreads][2];

        double currentNumber = minLearningRate;

        for (int i = 0; i < remainingItems; i++) {
            tasks[i][0] = currentNumber;
            tasks[i][1] = currentNumber + (itemsPerThread) * stepSizeLearningRate;
            currentNumber+= (itemsPerThread + 1) * stepSizeLearningRate;
            currentNumber = Math.round(currentNumber * 10) / 10.0;
        }

        for (int i = remainingItems; i < tasks.length - 1; i++) {
            tasks[i][0] = currentNumber;
            tasks[i][1] = currentNumber + (itemsPerThread -1) * stepSizeLearningRate;
            currentNumber+= itemsPerThread * stepSizeLearningRate;
            currentNumber = Math.round(currentNumber * 10) / 10.0;
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

        System.out.println("finding best parameters for: " + parameters.getMinLearningRate() + " to " + parameters.getMaxLearningRate());

        double currentMSE;

        double lowestMSE = Double.MAX_VALUE;
        TrainResult lowestMSEResult = null;

        ArrayList<TrainResult> results = new ArrayList<>();

        for (double lr = parameters.getMinLearningRate(); lr <= parameters.getMaxLearningRate(); lr+= Math.round(parameters.getStepSizeLearningRate() * 10.0) / 10.0) {
            for (int hn = parameters.getMinAmountOfHiddenNeurons(); hn <= parameters.getMaxAmountOfHiddenNeurons(); hn++) {
                for (double minw = parameters.getMinInitialWeightInterval()[0]; minw <= parameters.getMinInitialWeightInterval()[1]; minw+= parameters.getStepSizeWeight()) {
                    for (double maxw = parameters.getMaxInitialWeightInterval()[0]; maxw <= parameters.getMaxInitialWeightInterval()[1]; maxw+= parameters.getStepSizeWeight()) {
                        if (maxw < minw) {
                            continue;
                        }

                        for (double mint = parameters.getMinInitialTresholdInterval()[0]; mint <= parameters.getMinInitialTresholdInterval()[1]; mint+= parameters.getStepSizeWeight()) {
                            for (double maxt = parameters.getMaxInitialTreshldInterval()[0]; maxt <= parameters.getMaxInitialTreshldInterval()[1]; maxt += parameters.getStepSizeWeight()) {
                                neuralNetwork = new NeuralNetwork(currentNetwork.getInputLayer().size(), hn, currentNetwork.getOutputLayer().size(), lr, minw, maxw, mint, maxt);
                                currentMSE = this.trainNetwork(amountOfEpochs);

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
        }}

        return results;
    }

    public void createParameterFile(TrainParameters parameters, int amountOfEpochs, int amountOfThreads) {
        File file = new File(amountOfEpochs+ "epochs.txt");

        try {
            PrintWriter printWriter = new PrintWriter(file);

            printWriter.println("lr, " + "hn, " + "minw, " + "maxw, " + "mint, " + "maxt, " + "ep, " + "mse, " + "err");

            long startTime = System.currentTimeMillis();

            ArrayList<TrainResult> results = findBestParametersMultiThreaded(parameters, amountOfEpochs, amountOfThreads);

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
