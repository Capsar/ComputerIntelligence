package assignment1;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents a neural network
 * Created by Sam van Berkel on 12/09/2018.
 */
public class NeuralNetwork implements Serializable {
    private final int inputLayerSize;
    private final int hiddenLayerSize;
    private final int outputLayerSize;
    private final double learningRate;
    private final double minInitialWeight;
    private final double maxInitialWeight;
    private final double minInitialThreshold;
    private final double maxInitialThreshold;
    private ArrayList<Neuron> inputLayer;
    private ArrayList<Neuron> hiddenLayer;
    private ArrayList<Neuron> outputLayer;

    public NeuralNetwork(NeuralNetwork network) {
        inputLayerSize = network.inputLayerSize;
        hiddenLayerSize = network.hiddenLayerSize;
        outputLayerSize = network.outputLayerSize;
        learningRate = network.learningRate;
        minInitialWeight = network.minInitialWeight;
        maxInitialWeight = network.maxInitialWeight;
        minInitialThreshold = network.minInitialThreshold;
        maxInitialThreshold = network.maxInitialThreshold;
        inputLayer = network.inputLayer;
        hiddenLayer = network.hiddenLayer;
        outputLayer = network.outputLayer;
    }

    /**
     * Constructor for the neural network object.
     * @param inputLayerSize the amount of neurons in the input layer
     * @param hiddenLayerSize the amount of neurons in the hidden layer
     * @param outputLayerSize the amount of neurons in the output layer
     */
    public NeuralNetwork(int inputLayerSize, int hiddenLayerSize, int outputLayerSize, double learningRate, double minInitialWeight, double maxInitialWeight, double minInitialThreshold, double maxInitialThreshold) {
        this.inputLayerSize = inputLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputLayerSize = outputLayerSize;
        this.learningRate = learningRate;
        this.minInitialWeight = minInitialWeight;
        this.maxInitialWeight = maxInitialWeight;
        this.minInitialThreshold = minInitialThreshold;
        this.maxInitialThreshold = maxInitialThreshold;

        // Initialize the arrays for the neurons
        inputLayer = new ArrayList<>();
        hiddenLayer = new ArrayList<>();
        outputLayer = new ArrayList<>();


        // Create the neurons in the input layer (without connections)
        for (int i = 0; i < inputLayerSize; i++) {
            Neuron neuron = new Neuron(learningRate, minInitialThreshold, maxInitialThreshold);
            inputLayer.add(neuron);
        }

        // Create the neurons in the hidden layer (without connections)
        for (int i = 0; i < hiddenLayerSize; i++) {
            Neuron neuron = new Neuron(learningRate, minInitialThreshold, maxInitialThreshold);
            hiddenLayer.add(neuron);
        }

        // Create the neurons in the output layer (without connections)
        for (int i = 0; i < outputLayerSize; i++) {
            Neuron neuron = new Neuron(learningRate, minInitialThreshold, maxInitialThreshold);
            outputLayer.add(neuron);
        }

        // Create the connections between the input layer and the hidden layer
        for (Neuron currentNeuron : inputLayer) {
            for (Neuron destination : hiddenLayer) {
                // Create a new connection between the input layer neuron and the hiddenlayer neuron and add it to both neurons
                Connection connection = new Connection(currentNeuron, destination, minInitialWeight, maxInitialWeight);

                currentNeuron.addOutput(connection);
                destination.addInput(connection);

            }
        }

        // Create the connections between the hidden layer and the output layer
        for (Neuron currentNeuron : hiddenLayer) {
            for (Neuron destination : outputLayer) {
                Connection connection = new Connection(currentNeuron, destination, minInitialWeight, maxInitialWeight);

                currentNeuron.addOutput(connection);
                destination.addInput(connection);
            }
        }
    }

    /**
     * Computes the outputs of the output layer from a list of inputs given to the input layer.
     * @param inputs inputs given to the input layer
     * @return list of doubles which represent the outputs of the output layer
     */
    public double[] computeOutput(double[] inputs) {
        for (int i = 0; i < inputLayer.size(); i++) {
            inputLayer.get(i).setLastOutput(inputs[i]);
        }

        double[] hiddenLayerResults = new double[hiddenLayer.size()];

        for (int i = 0; i < hiddenLayer.size(); i++) {
            hiddenLayerResults[i] = hiddenLayer.get(i).computeOutput(inputs);
        }

        double[] finalResults = new double[outputLayer.size()];

        for (int i = 0; i < outputLayer.size(); i++) {
            finalResults[i] = outputLayer.get(i).computeOutput(hiddenLayerResults);
        }

        return finalResults;
    }

    /**
     * Trains the neuroal network by first computing the output from a set of inputs and then updating the weights and tresholds.
     * @param input the inputs for the network
     * @param desired the desired outputs of the network
     * @return the mean squared error
     */
    public void trainNetwork(double[] input, double[] desired) {
        this.computeOutput(input);

        for (int i = 0; i < outputLayer.size(); i++) {
            outputLayer.get(i).computeErrorGradientOutputLayer(desired[i]);

            for (int j = 0; j < outputLayer.get(i).getInputs().size(); j++) {
                outputLayer.get(i).getInputs().get(j).adjustWeight();
            }

            outputLayer.get(i).updateThreshold();
        }

        for (int i = 0; i < hiddenLayer.size(); i++) {
            hiddenLayer.get(i).computeErrorGradientHiddenLayer();

            for (int j = 0; j < hiddenLayer.get(i).getInputs().size(); j++) {
                hiddenLayer.get(i).getInputs().get(j).adjustWeight();
            }

            hiddenLayer.get(i).updateThreshold();
        }
    }

    public double calculateMSE(double[] input, double[] desired) {
        double meanSquaredError = 0;
        this.computeOutput(input);
        for (int i = 0; i < outputLayer.size(); i++) {
            outputLayer.get(i).computeErrorGradientOutputLayer(desired[i]);
            meanSquaredError += Math.pow(outputLayer.get(i).getLastError(), 2);
        }
        return meanSquaredError;
    }

    public void print() {
        System.out.println("Input neurons=" + this.inputLayerSize + ", hidden neurons=" + this.hiddenLayerSize + ", output neurons=" + this.outputLayerSize);
        System.out.println("input thresholds:");
        inputLayer.forEach(neuron -> System.out.print(neuron.getThreshold() + ", "));
        System.out.println("\n\nhidden thresholds:");
        hiddenLayer.forEach(neuron -> System.out.print(neuron.getThreshold() + ", "));
        System.out.println("\n\noutput thresholds:");
        outputLayer.forEach(neuron -> System.out.print(neuron.getThreshold() + ", "));
        System.out.println("\n\nweights of connections from input to hidden:");
        inputLayer.forEach(neuron -> {
            System.out.println("\nNeuron with threshold=" + neuron.getThreshold());
            neuron.getOutputs().forEach(c -> System.out.println(c.getWeight() + ", "));
        });
        System.out.println("\n\nweights of connections from hidden to output:");
        hiddenLayer.forEach(neuron -> {
            System.out.println("\nNeuron with threshold=" + neuron.getThreshold());
            neuron.getOutputs().forEach(c -> System.out.println(c.getWeight() + ", "));
        });
    }
}
