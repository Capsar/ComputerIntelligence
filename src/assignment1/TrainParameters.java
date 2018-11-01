package assignment1;

/**
 * Created by Sam van Berkel on 19/09/2018.
 */
public class TrainParameters {
    int minAmountOfHiddenNeurons;
    int maxAmountOfHiddenNeurons;
    double minLearningRate;
    double maxLearningRate;
    double stepSizeLearningRate;
    double[] minInitialWeightInterval;
    double[] maxInitialWeightInterval;
    double[] minInitialTresholdInterval;
    double[] maxInitialTreshldInterval;
    double stepSizeWeight;

    public TrainParameters(int minAmountOfHiddenNeurons, int maxAmountOfHiddenNeurons, double minLearningRate, double maxLearningRate, double stepSizeLearningRate, double[] minInitialWeightInterval, double[] maxInitialWeightInterval, double[] minInitialTresholdInterval, double[] maxInitialTreshldInterval, double stepSizeWeight) {
        this.minAmountOfHiddenNeurons = minAmountOfHiddenNeurons;
        this.maxAmountOfHiddenNeurons = maxAmountOfHiddenNeurons;
        this.minLearningRate = minLearningRate;
        this.maxLearningRate = maxLearningRate;
        this.stepSizeLearningRate = stepSizeLearningRate;
        this.minInitialWeightInterval = minInitialWeightInterval;
        this.maxInitialWeightInterval = maxInitialWeightInterval;
        this.minInitialTresholdInterval = minInitialTresholdInterval;
        this.maxInitialTreshldInterval = maxInitialTreshldInterval;
        this.stepSizeWeight = stepSizeWeight;
    }

    public int getMinAmountOfHiddenNeurons() {
        return minAmountOfHiddenNeurons;
    }

    public int getMaxAmountOfHiddenNeurons() {
        return maxAmountOfHiddenNeurons;
    }

    public double getMinLearningRate() {
        return minLearningRate;
    }

    public double getMaxLearningRate() {
        return maxLearningRate;
    }

    public double getStepSizeLearningRate() {
        return stepSizeLearningRate;
    }

    public double[] getMinInitialWeightInterval() {
        return minInitialWeightInterval;
    }

    public double[] getMaxInitialWeightInterval() {
        return maxInitialWeightInterval;
    }

    public double[] getMinInitialTresholdInterval() {
        return minInitialTresholdInterval;
    }

    public double[] getMaxInitialTreshldInterval() {
        return maxInitialTreshldInterval;
    }

    public double getStepSizeWeight() {
        return stepSizeWeight;
    }

    public void setMinLearningRate(double minLearningRate) {
        this.minLearningRate = minLearningRate;
    }

    public void setMaxLearningRate(double maxLearningRate) {
        this.maxLearningRate = maxLearningRate;
    }
}
