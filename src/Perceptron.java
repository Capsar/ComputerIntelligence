import java.util.Random;

/**
 * Perceptron class
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Perceptron {
    private double[] weights;
    private double treshold;
    private double learningRate;

    /**
     * Constructor for the perceptron object
     * @param treshold the treshold for the output
     * @param learningRate the learning rate while training the perceptron
     */
    public Perceptron(double treshold, double learningRate) {
        for (int i = 0; i < weights.length; i++) {
            this.weights[i] = -1 + Math.random() * (2);
        }
        this.treshold = treshold;
        this.learningRate = learningRate;
    }

    /**
     * Computes the output of the perceptron based on the input, weights and activation function
     * @param inputs the inputs
     * @return the output of the perceptron
     */
    public double computeOutput(double[] inputs) {
        double sum = 0;

        for (int i = 0; i < weights.length; i++) {
            sum+= inputs[i] * weights[i];
        }

        double result = sum - treshold;

        return Activation.Sign(result);
    }

    /**
     * Trains the perceptron by updating the weights
     * @param inputs the inputs for the perceptron
     * @param desired the desired result of the given inputs
     */
    public void train(double[] inputs, double desired) {
        double result = computeOutput(inputs);
        double error = desired - result;

        // Update every weight with the formula newWeight = oldWeight + error * input
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] + error * inputs[i] * learningRate;
        }
    }
}
