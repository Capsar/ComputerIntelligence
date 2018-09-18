import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Sam van Berkel on 15/09/2018.
 */
public class ConnectionTest {
    NeuralNetwork neuralNetwork;

    @BeforeEach
    public void setUp() {
        neuralNetwork = new NeuralNetwork(2, 2, 1, 0.1);

        neuralNetwork.setWeightsInputHidden(new double[]{0.5, 0.9, 0.4, 1.0});
        neuralNetwork.setWeightsHiddenOutput(new double[]{-1.2, 1.1});

        neuralNetwork.setTresholdsHiddenLayer(new double[]{0.8, -0.1});
        neuralNetwork.setTresholdsOutputLayer(new double[]{0.3});
    }

    @Test
    public void adjustWeight() {
        neuralNetwork.computeOutput(new double[]{1, 1});

        neuralNetwork.getHiddenLayer().get(0).computeErrorGradientOutputLayer(0);
        neuralNetwork.getHiddenLayer().get(0).getInputs().get(0).adjustWeight();

        assertEquals(0.5350, neuralNetwork.getHiddenLayer().get(0));
        assertEquals(-1.2067, neuralNetwork.getOutputLayer().get(0).getInputs().get(0).getWeight());
    }
}
