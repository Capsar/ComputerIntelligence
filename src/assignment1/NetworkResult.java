package assignment1;

import java.io.Serializable;
import java.util.ArrayList;

public class NetworkResult implements Serializable {
    private static final long serialVersionUID = 0L;

    private float percentageValidation;
    private float percentageTest;
    private ArrayList<TrainTarget> trainingSet;
    private NeuralNetwork network;
    private float[] checkValidation;
    private float[] checkTest;

    public NetworkResult(NeuralNetwork network, float[] checkValidation, float[] checkTest, ArrayList<TrainTarget> trainingSet) {
        this.network = network;
        this.checkValidation = checkValidation;
        this.checkTest = checkTest;
        this.percentageValidation = checkValidation[1];
        this.percentageTest = checkTest[1];

        this.trainingSet = trainingSet;
    }

    public float[] getCheckValidation() {
        return checkValidation;
    }

    public float[] getCheckTest() {
        return checkTest;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }

    public float getPercentageValidation() {
        return percentageValidation;
    }

    public float getPercentageTest() {
        return percentageTest;
    }

    public ArrayList<TrainTarget> getTrainingSet() {
        return trainingSet;
    }
}
