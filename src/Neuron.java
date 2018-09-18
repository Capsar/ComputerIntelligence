import java.util.ArrayList;

/**
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Neuron {
    private ArrayList<Connection> inputs;
    private ArrayList<Connection> outputs;
    private double treshold;
    private double learningRate;
    private double lastOutput;
    private double lastError;
    private double lastErrorGradient;

    public Neuron(ArrayList<Connection> inputs, ArrayList<Connection> outputs, double learningRate) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.treshold = Math.random();
        this.learningRate = learningRate;
        lastOutput = 0;
    }

    public ArrayList<Connection> getInputs() {
        return inputs;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public double getLastOutput() {
        return lastOutput;
    }

    public double getLastError() {
        return lastError;
    }

    public double getLastErrorGradient() {
        return lastErrorGradient;
    }

    public void setInputs(ArrayList<Connection> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<Connection> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<Connection> outputs) {
        this.outputs = outputs;
    }

    public void setTreshold(double treshold) {
        this.treshold = treshold;
    }

    public void setLastOutput(double lastOutput) {
        this.lastOutput = lastOutput;
    }

    public void addInput(Connection connection) {
        inputs.add(connection);
    }

    public void addOutput(Connection connection) {
        outputs.add(connection);
    }

    /**
     * Computes the output of the neuron given a list of inputs.
     * @param inputValues list of inputs
     * @return the output of the neuron
     */
    public double computeOutput(double[] inputValues) {
        double sum = 0;

        for (int i = 0; i < inputs.size(); i++) {
            sum+= inputValues[i] * inputs.get(i).getWeight();
        }

        double result = sum - treshold;

        double output = Activation.Sigmoid(result);
        lastOutput = output;

        return output;
    }

    /**
     * Computes the error between the output and the desired value.
     * @param inputValues list of inputs that will be used to calculate the output
     * @param desired the desired output of the neuron
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
     * @return the error gradient of the neuron
     */
    public double computeErrorGradientHiddenLayer(){
        double nextLayer = 0;

        for (int i = 0; i < outputs.size(); i++) {
            nextLayer+= outputs.get(i).getEnd().getLastErrorGradient() * outputs.get(i).getWeight();
        }

        double errorGradient = lastOutput * (1 - lastOutput) * nextLayer;

        lastErrorGradient = errorGradient;

        return errorGradient;
    }

    /**
     * Update the treshold of the neuron using the error gradient.
     */
    public void updateTreshold() {
        double deltaTreshold = learningRate * -1 * lastErrorGradient;
        treshold+= deltaTreshold;
    }
}
