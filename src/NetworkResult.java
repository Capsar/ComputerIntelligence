public class NetworkResult {

    private NeuralNetwork network;
    private float percentageIncorrect;
    private float[] checkValidation;
    private float[] checkTest;

    public NetworkResult(NeuralNetwork network, float percentageIncorrect) {
        this.network = network;
        this.percentageIncorrect = percentageIncorrect;
    }

    public NetworkResult(NeuralNetwork network, float[] checkValidation, float[] checkTest) {
        this.network = network;
        this.checkValidation = checkValidation;
        this.checkTest = checkTest;
    }

    public float[] getCheckValidation() {
        return checkValidation;
    }

    public float[] getCheckTest() {
        return checkTest;
    }

    public float getPercentageIncorrect() {
        return percentageIncorrect;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }
}
