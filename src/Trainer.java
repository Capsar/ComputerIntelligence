import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

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
    public Trainer(NeuralNetwork neuralNetwork, ArrayList<TrainTarget> trainData,double desiredMSE) {
        this.neuralNetwork = neuralNetwork;
        this.desiredMSE = desiredMSE;
        currentEpoch = 1;
        currentDataIndex = 0;
        currentMSE = Integer.MAX_VALUE;
        this.trainData = trainData;
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

    public static double round(double number) {
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public void trainNetwork() {
        while (currentMSE > desiredMSE) {
            currentMSE = neuralNetwork.trainNetwork(trainData.get(currentDataIndex).getInputs(), trainData.get(currentDataIndex).getDesiredOutputs());
            double[] currentValues = neuralNetwork.computeOutput(trainData.get(currentDataIndex).getInputs());

            System.out.println("current data item: " + currentDataIndex);
            System.out.println("current epoch: " + currentEpoch);
            System.out.println("");
            System.out.println("current input: " + outputToString(trainData.get(currentDataIndex).getInputs()));
            System.out.println("current output: " + outputToString(currentValues));
            System.out.println("current target: " + getTarget(currentValues));
            System.out.println("");
            System.out.println("current mse: " + currentMSE);
            currentDataIndex++;

            if (currentDataIndex + 1 > trainData.size()) {
                currentEpoch++;
                currentDataIndex = 0;
            }
        }
        System.out.println("training finished, mse: " + currentMSE);
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
}
