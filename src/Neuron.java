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

    public double computeError(double[] inputValues, double desired) {
        double result = computeOutput(inputValues);
        double error = desired - result;

        return error;

    }

    public double computeErrorGradientOutputLayer(double desired) {
        double[] inputValues = new double[inputs.size()];

        for (int i = 0; i < inputs.size(); i++) {
            inputValues[i] = inputs.get(i).getStart().getLastOutput();
        }

        double errorGradient = lastOutput * (1 - lastOutput) * computeError(inputValues, desired);
        lastErrorGradient = errorGradient;

        System.out.println("error gradient output layer: " + errorGradient);

        return errorGradient;
    }

    public double computeErrorGradientHiddenLayer(){
        double errorGradient = lastOutput * (1 - lastOutput) * outputs.get(0).getEnd().getLastErrorGradient() * outputs.get(0).getWeight();

        lastErrorGradient = errorGradient;

        System.out.println("error gradient hidden layer: " + errorGradient);

        return errorGradient;
    }
}
