package assignment1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Neuron implements Serializable {
    private List<Connection> inputs;
    private List<Connection> outputs;
    private double threshold;
    private double learningRate;
    private double lastOutput;
    private double lastError;
    private double lastErrorGradient;

    public Neuron(double learningRate, double minInitialTreshold, double maxInitialTreshold) {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.threshold = minInitialTreshold + Math.random() * (maxInitialTreshold - minInitialTreshold);
        this.learningRate = learningRate;
        lastOutput = 0;
    }

    public List<Connection> getInputs() {
        return inputs;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double getLastOutput() {
        return lastOutput;
    }

    public void setLastOutput(double lastOutput) {
        this.lastOutput = lastOutput;
    }

    public double getLastError() {
        return lastError;
    }

    public double getLastErrorGradient() {
        return lastErrorGradient;
    }

    public double getThreshold() {
        return threshold;
    }

    public List<Connection> getOutputs() {
        return outputs;
    }

    public void addInput(Connection connection) {
        inputs.add(connection);
    }

    public void addOutput(Connection connection) {
        outputs.add(connection);
    }

    /**
     * Computes the output of the neuron given a list of inputs.
     *
     * @param inputValues list of inputs
     * @return the output of the neuron
     */
    public double computeOutput(double[] inputValues) {
        double sum = 0;

        for (int i = 0; i < inputs.size(); i++) {
            sum += inputValues[i] * inputs.get(i).getWeight();
        }

        double result = sum - threshold;

        double output = Activation.Sigmoid(result);
        lastOutput = output;

        return output;
    }

    /**
     * Computes the error between the output and the desired value.
     *
     * @param inputValues list of inputs that will be used to calculate the output
     * @param desired     the desired output of the neuron
     * @return the error for the output
     */
    public double computeError(double[] inputValues, double desired) {
        double result = computeOutput(inputValues);
        double error = desired - result;

        lastError = error;

        return error;

    }

    /**
     * Computes the error gradient for a neuron that is in the output layer.
     *
     * @param desired the desired output of the neuron
     * @return the error gradient of the neuron
     */
    public double computeErrorGradientOutputLayer(double desired) {
        double[] inputValues = new double[inputs.size()];

        for (int i = 0; i < inputs.size(); i++) {
            inputValues[i] = inputs.get(i).getStart().getLastOutput();
        }

        double errorGradient = lastOutput * (1 - lastOutput) * computeError(inputValues, desired);
        lastErrorGradient = errorGradient;

        return errorGradient;
    }

    /**
     * Computes the error gradient for a neuron that is in the hidden layer.
     *
     * @return the error gradient of the neuron
     */
    public double computeErrorGradientHiddenLayer() {
        double nextLayer = 0;

        for (int i = 0; i < outputs.size(); i++) {
            nextLayer += outputs.get(i).getEnd().getLastErrorGradient() * outputs.get(i).getWeight();
        }

        double errorGradient = lastOutput * (1 - lastOutput) * nextLayer;

        lastErrorGradient = errorGradient;

        return errorGradient;
    }

    /**
     * Update the threshold of the neuron using the error gradient.
     */
    public void updateThreshold() {
        double deltaThreshold = learningRate * -1 * lastErrorGradient;
        threshold += deltaThreshold;
    }
}
