/**
 * Created by Sam van Berkel on 19/09/2018.
 */
public class TrainResult {
    private int amountOfHiddenNeurons;
    private double learningRate;
    private double minInitialWeight;
    private double maxInitialWeight;
    private double minInitialTreshold;
    private double maxInitialTreshld;
    private double epochs;
    private double MSE;
    private double errors;

    public TrainResult(int amountOfHiddenNeurons, double learningRate, double minInitialWeight, double maxInitialWeight, double minInitialTreshold, double maxInitialTreshld, double epochs, double MSE, double errors) {
        this.amountOfHiddenNeurons = amountOfHiddenNeurons;
        this.learningRate = learningRate;
        this.minInitialWeight = minInitialWeight;
        this.maxInitialWeight = maxInitialWeight;
        this.minInitialTreshold = minInitialTreshold;
        this.maxInitialTreshld = maxInitialTreshld;
        this.epochs = epochs;
        this.MSE = MSE;
        this.errors = errors;
    }

    public double getLearningRate() {
        return learningRate;
    }

    @Override
    public String toString() {
        return learningRate +
                ", " + amountOfHiddenNeurons +
                ", " + MSE +
                ", " + minInitialWeight +
                ", " + maxInitialWeight +
                ", " + minInitialTreshold +
                ", " + maxInitialTreshld +
                ", " + epochs;
    }
}
