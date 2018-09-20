/**
 * Created by Sam van Berkel on 19/09/2018.
 */
public class TrainResult {
    int AmountOfHiddenNeurons;
    double learningRate;
    double minInitialWeight;
    double maxInitialWeight;
    double minInitialTreshold;
    double maxInitialTreshld;
    double epochs;
    double MSE;
    double errors;

    public TrainResult(int amountOfHiddenNeurons, double learningRate, double minInitialWeight, double maxInitialWeight, double minInitialTreshold, double maxInitialTreshld, double epochs, double MSE, double errors) {
        AmountOfHiddenNeurons = amountOfHiddenNeurons;
        this.learningRate = learningRate;
        this.minInitialWeight = minInitialWeight;
        this.maxInitialWeight = maxInitialWeight;
        this.minInitialTreshold = minInitialTreshold;
        this.maxInitialTreshld = maxInitialTreshld;
        this.epochs = epochs;
        this.MSE = MSE;
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "TrainResult{" +
                "hn=" + AmountOfHiddenNeurons +
                ", lre=" + learningRate +
                ", minw=" + minInitialWeight +
                ", maxw=" + maxInitialWeight +
                ", mint=" + minInitialTreshold +
                ", maxt=" + maxInitialTreshld +
                ", epochs=" + epochs +
                ", MSE=" + MSE +
                ", errors=" + errors +
                '}';
    }
}
