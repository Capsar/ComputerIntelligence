package assignment1;

import java.io.Serializable;

/**
 * Products class that contains a single line of the training files
 * Created by Sam van Berkel on 12/09/2018.
 */
public class TrainTarget implements Serializable {
    private double[] inputs;
    private double[] desiredOutputs;

    public TrainTarget(double[] inputs, double[] desiredOutputs) {
        this.inputs = inputs;
        this.desiredOutputs = desiredOutputs;
    }

    public double[] getInputs() {
        return inputs;
    }

    public double[] getDesiredOutputs() {
        return desiredOutputs;
    }
}
