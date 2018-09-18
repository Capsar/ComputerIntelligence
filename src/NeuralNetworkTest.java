import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Sam van Berkel on 13/09/2018.
 */
public class NeuralNetworkTest {
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
    public void setWeightsInputHiddenTest() {
        assertEquals(neuralNetwork.getInputLayer().get(0).getOutputs().get(0).getWeight(), 0.5);
        assertEquals(neuralNetwork.getInputLayer().get(0).getOutputs().get(1).getWeight(), 0.9);
        assertEquals(neuralNetwork.getInputLayer().get(1).getOutputs().get(0).getWeight(), 0.4);
        assertEquals(neuralNetwork.getInputLayer().get(1).getOutputs().get(1).getWeight(), 1.0);

        assertEquals(neuralNetwork.getHiddenLayer().get(0).getInputs().get(0).getWeight(), 0.5);
        assertEquals(neuralNetwork.getHiddenLayer().get(0).getInputs().get(1).getWeight(), 0.4);
        assertEquals(neuralNetwork.getHiddenLayer().get(1).getInputs().get(0).getWeight(), 0.9);
        assertEquals(neuralNetwork.getHiddenLayer().get(1).getInputs().get(1).getWeight(), 1.0);
    }

    @Test
    public void setWeightsHiddenOutputTest() {
        assertEquals( -1.2, neuralNetwork.getHiddenLayer().get(0).getOutputs().get(0).getWeight());
        assertEquals( 1.1, neuralNetwork.getHiddenLayer().get(1).getOutputs().get(0).getWeight());
    }

    @Test
    public void computeOutput() {
        double[] result = neuralNetwork.computeOutput(new double[]{1, 1});

        assertEquals(0.5097, Trainer.round(result[0]));
        assertEquals(0.5250, Trainer.round(neuralNetwork.getHiddenLayer().get(0).getLastOutput()));
        assertEquals(0.8808, Trainer.round(neuralNetwork.getHiddenLayer().get(1).getLastOutput()));
    }

    @Test
    public void trainNetworkTest() {
        double meanSquaredError = neuralNetwork.trainNetwork(new double[]{1, 1}, new double[]{0});

        assertEquals(0.5097, Trainer.round(neuralNetwork.getOutputLayer().get(0).getLastOutput()));
        assertEquals(0.5250, Trainer.round(neuralNetwork.getHiddenLayer().get(0).getLastOutput()));
        assertEquals(0.8808, Trainer.round(neuralNetwork.getHiddenLayer().get(1).getLastOutput()));

        assertEquals(-1.2067, Trainer.round(neuralNetwork.getOutputLayer().get(0).getInputs().get(0).getWeight()));
        assertEquals(1.0888, Trainer.round(neuralNetwork.getOutputLayer().get(0).getInputs().get(1).getWeight()));

        assertEquals(0.5038, Trainer.round(neuralNetwork.getHiddenLayer().get(0).getInputs().get(0).getWeight()));
        assertEquals(0.8985, Trainer.round(neuralNetwork.getHiddenLayer().get(1).getInputs().get(0).getWeight()));
        assertEquals(0.4038, Trainer.round(neuralNetwork.getHiddenLayer().get(0).getInputs().get(1).getWeight()));
        assertEquals(0.9985, Trainer.round(neuralNetwork.getHiddenLayer().get(1).getInputs().get(1).getWeight()));

        assertEquals(0.2598, Trainer.round(meanSquaredError));


    }
}
