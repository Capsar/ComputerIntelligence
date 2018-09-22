public class NetworkResult {

    private float percentageVali;
    private float percentageTest;
    private NeuralNetwork network;
    private float[] checkValidation;
    private float[] checkTest;

    public NetworkResult(NeuralNetwork network, float percentageIncorrect) {
        this.network = network;
    }

    public NetworkResult(NeuralNetwork network, float[] checkValidation, float[] checkTest) {
        this.network = network;
        this.checkValidation = checkValidation;
        this.checkTest = checkTest;
        this.percentageVali = checkValidation[1];
        this.percentageTest = checkTest[1];

    }

    public float[] getCheckValidation() {
        return checkValidation;
    }

    public float[] getCheckTest() {
        return checkTest;
    }

    public NeuralNetwork getNetwork() {
        return network;
    }

    public float getPercentageVali() {
        return percentageVali;
    }

    public float getPercentageTest() {
        return percentageTest;
    }
}
