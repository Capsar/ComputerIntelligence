import java.util.ArrayList;

/**
 * Class that represents a neural network
 * Created by Sam van Berkel on 12/09/2018.
 */
public class NeuralNetwork {
    private final int inputLayerSize;
    private final int hiddenLayerSize;
    private final int outputLayerSize;
    private final double learningRate;
    private final double minInitialWeight;
    private final double maxInitialWeight;
    private final double minInitialTreshold;
    private final double maxInitialTreshold;
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
        minInitialTreshold = network.minInitialTreshold;
        maxInitialTreshold = network.maxInitialTreshold;
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
    public NeuralNetwork(int inputLayerSize, int hiddenLayerSize, int outputLayerSize, double learningRate, double minInitialWeight, double maxInitialWeight, double minInitialTreshold, double maxInitialTreshold) {
        this.inputLayerSize = inputLayerSize;
        this.hiddenLayerSize = hiddenLayerSize;
        this.outputLayerSize = outputLayerSize;
        this.learningRate = learningRate;
        this.minInitialWeight = minInitialWeight;
        this.maxInitialWeight = maxInitialWeight;
        this.minInitialTreshold = minInitialTreshold;
        this.maxInitialTreshold = maxInitialTreshold;

        // Initialize the arrays for the neurons
        inputLayer = new ArrayList<Neuron>();
        hiddenLayer = new ArrayList<Neuron>();
        outputLayer = new ArrayList<Neuron>();


        // Create the neurons in the input layer (without connections)
        for (int i = 0; i < inputLayerSize; i++) {
            Neuron neuron = new Neuron(new ArrayList<Connection>(), new ArrayList<Connection>(), learningRate, minInitialTreshold, maxInitialTreshold);
            inputLayer.add(neuron);
        }

        // Create the neurons in the hidden layer (without connections)
        for (int i = 0; i < hiddenLayerSize; i++) {
            Neuron neuron = new Neuron(new ArrayList<Connection>(), new ArrayList<Connection>(),learningRate, minInitialTreshold, maxInitialTreshold);
            hiddenLayer.add(neuron);
        }

        // Create the neurons in the output layer (without connections)
        for (int i = 0; i < outputLayerSize; i++) {
            Neuron neuron = new Neuron(new ArrayList<Connection>(), new ArrayList<Connection>(), learningRate, minInitialTreshold, maxInitialTreshold);
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

    public ArrayList<Neuron> getInputLayer() {
        return inputLayer;
    }

    public ArrayList<Neuron> getHiddenLayer() {
        return hiddenLayer;
    }

    public ArrayList<Neuron> getOutputLayer() {
        return outputLayer;
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

    public double getMinInitialWeight() {
        return minInitialWeight;
    }

    public double getMaxInitialWeight() {
        return maxInitialWeight;
    }

    public double getMinInitialTreshold() {
        return minInitialTreshold;
    }

    public double getMaxInitialTreshold() {
        return maxInitialTreshold;
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

    @Override
    public NeuralNetwork clone(){
        try {
            return (NeuralNetwork) super.clone();
        }catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return new NeuralNetwork(this);
        }
    }

    public void print() {
        System.out.println("Input neurons=" + this.inputLayerSize + ", hidden neurons=" + this.hiddenLayerSize + ", output neurons=" + this.outputLayerSize);
        System.out.println("input thresholds:");
        inputLayer.forEach(neuron -> System.out.print(neuron.getTreshold() + ", "));
        System.out.println("\n\nhidden thresholds:");
        hiddenLayer.forEach(neuron -> System.out.print(neuron.getTreshold() + ", "));
        System.out.println("\n\noutput thresholds:");
        outputLayer.forEach(neuron -> System.out.print(neuron.getTreshold() + ", "));
        System.out.println("\n\nweights of connections from input to hidden:");
        inputLayer.forEach(neuron -> {
            System.out.println("\nNeuron with threshold=" + neuron.getTreshold());
            neuron.getOutputs().forEach(c -> System.out.println(c.getWeight() + ", "));
        });
        System.out.println("\n\nweights of connections from hidden to output:");
        hiddenLayer.forEach(neuron -> {
            System.out.println("\nNeuron with threshold=" + neuron.getTreshold());
            neuron.getOutputs().forEach(c -> System.out.println(c.getWeight() + ", "));
        });
    }
}
