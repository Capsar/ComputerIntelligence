import java.util.ArrayList;

public class NetworkResult {

    private NeuralNetwork network;
    private float percentageIncorrect;

    public NetworkResult(NeuralNetwork network, float percentageIncorrect) {
        this.network = network;
        this.percentageIncorrect = percentageIncorrect;
    }

    public float getPercentageIncorrect() {
        return percentageIncorrect;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }
}
