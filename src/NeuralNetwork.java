import java.util.ArrayList;

/**
 * Created by Sam van Berkel on 12/09/2018.
 */
public class NeuralNetwork {
    private final int inputLayerSize;
    private final int hiddenLayerSize;
    private final int outputLayerSize;
    private final double learningRate;
    ArrayList<Neuron> inputLayer;
    ArrayList<Neuron> hiddenLayer;
    ArrayList<Neuron> outputLayer;

    public ArrayList<Neuron> getInputLayer() {
        return inputLayer;
    }

    public ArrayList<Neuron> getHiddenLayer() {
        return hiddenLayer;
    }

    public ArrayList<Neuron> getOutputLayer() {
        return outputLayer;
    }

    /**
     * Constructor for the neural network object.
     * @param inputLayerSize the amount of neurons in the input layer
     * @param hiddenLayerSize the amount of neurons in the hidden layer
     * @param outputLayerSize the amount of neurons in the output layer
     */
    public NeuralNetwork(int inputLayerSize, int hiddenLayerSize, int outputLayerSize, double learningRate) {
        this.inputLayerSize = inputLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputLayerSize = outputLayerSize;
        this.learningRate = learningRate;

        // Initialize the arrays for the neurons
        inputLayer = new ArrayList<Neuron>();
        hiddenLayer = new ArrayList<Neuron>();
        outputLayer = new ArrayList<Neuron>();


        // Create the neurons in the input layer (without connections)
        for (int i = 0; i < inputLayerSize; i++) {
            Neuron neuron = new Neuron(new ArrayList<Connection>(), new ArrayList<Connection>(), learningRate);
            inputLayer.add(neuron);
        }

        // Create the neurons in the hidden layer (without connections)
        for (int i = 0; i < hiddenLayerSize; i++) {
            Neuron neuron = new Neuron(new ArrayList<Connection>(), new ArrayList<Connection>(),learningRate);
            hiddenLayer.add(neuron);
        }

        // Create the neurons in the output layer (without connections)
        for (int i = 0; i < outputLayerSize; i++) {
            Neuron neuron = new Neuron(new ArrayList<Connection>(), new ArrayList<Connection>(), learningRate);
            outputLayer.add(neuron);
        }

        // Create the connections between the input layer and the hidden layer
        for (Neuron currentNeuron : inputLayer) {
            for (Neuron destination : hiddenLayer) {
                // Create a new connection between the input layer neuron and the hiddenlayer neuron and add it to both neurons
                Connection connection = new Connection(currentNeuron, destination);

                currentNeuron.addOutput(connection);
                destination.addInput(connection);

            }
        }

        // Create the connections between the hidden layer and the output layer
        for (Neuron currentNeuron : hiddenLayer) {
            for (Neuron destination : outputLayer) {
                Connection connection = new Connection(currentNeuron, destination);

                currentNeuron.addOutput(connection);
                destination.addInput(connection);
            }
        }
    }

    /**
     * Sets the weights for the connections between the input layer and the hidden layer.
     * @param weights the weights that will be set
     */
    public void setWeightsInputHidden(double[] weights) {
        int amountOfConnections = hiddenLayer.size() * inputLayer.size();

        if (amountOfConnections != weights.length) {
            System.out.println("Wrong amount of connections between input and hidden!");
            return;
        }

        int index = 0;

        // Create the connections between the input layer and the hidden layer
        for (int i = 0; i < inputLayer.size(); i++) {
            for (int j = 0; j < hiddenLayer.size(); j++) {
                // Create a new connection between the input layer neuron and the hiddenlayer neuron and add it to both neurons
                Neuron currentInputNeuron = inputLayer.get(i);
                Neuron currentHiddenNeuron = hiddenLayer.get(j);

                currentInputNeuron.getOutputs().get(j).setWeight(weights[index]);
                currentHiddenNeuron.getInputs().get(i).setWeight(weights[index]);

                index++;
            }
        }
    }

    /**
     * Sets the weights for the connections between the hidden layer and the output layer.
     * @param weights the weights that will be set
     */
    public void setWeightsHiddenOutput(double[] weights) {
        int amountOfConnections = hiddenLayer.size() * outputLayer.size();

        if (amountOfConnections != weights.length) {
            System.out.println("Wrong amount of connections between hidden and output!");
            return;
        }

        int index = 0;

        // Create the connections between the input layer and the hidden layer
        for (int i = 0; i < hiddenLayer.size(); i++) {
            for (int j = 0; j < outputLayer.size(); j++) {
                // Create a new connection between the input layer neuron and the hiddenlayer neuron and add it to both neurons
                Neuron currentHiddenNeuron = hiddenLayer.get(i);
                Neuron currentOutputNeuron = outputLayer.get(j);

                currentHiddenNeuron.getOutputs().get(j).setWeight(weights[index]);
                currentOutputNeuron.getInputs().get(i).setWeight(weights[index]);

                index++;
            }
        }
    }

    /**
     * Sets the tresholds of the neurons in the hidden layer.
     * @param tresholds the tresholds that will be set
     */
    public void setTresholdsHiddenLayer(double[] tresholds) {
        for (int i = 0; i < hiddenLayer.size(); i++) {
            hiddenLayer.get(i).setTreshold(tresholds[i]);
        }
    }

    /**
     * Sets the tresholds of the neurons in the output layer.
     * @param tresholds the tresholds that will be set
     */
    public void setTresholdsOutputLayer(double[] tresholds) {
        for (int i = 0; i < outputLayer.size(); i++) {
            outputLayer.get(i).setTreshold(tresholds[i]);
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

            outputLayer.get(i).updateTreshold();
        }

        for (int i = 0; i < hiddenLayer.size(); i++) {
            hiddenLayer.get(i).computeErrorGradientHiddenLayer();

            for (int j = 0; j < hiddenLayer.get(i).getInputs().size(); j++) {
                hiddenLayer.get(i).getInputs().get(j).adjustWeight();
            }

            hiddenLayer.get(i).updateTreshold();
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

    public int getInputLayerSize() {
        return inputLayerSize;
    }

    public int getHiddenLayerSize() {
        return hiddenLayerSize;
    }

    public int getOutputLayerSize() {
        return outputLayerSize;
    }

    public double getLearningRate() {
        return learningRate;
    }
}
