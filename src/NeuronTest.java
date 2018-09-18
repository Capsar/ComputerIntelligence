import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Sam van Berkel on 13/09/2018.
 */
public class NeuronTest {
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
    public void computeOutputTest() {
        Neuron neuron = neuralNetwork.getOutputLayer().get(0);

        double[] input = new double[]{0.5250, 0.8808};

        System.out.println(neuron.computeOutput(input));

        assertEquals(0.5097, Trainer.round(neuron.computeOutput(input)));
    }

    @Test
    public void computeErrorGradientOutputLayerTest() {
        neuralNetwork.computeOutput(new double[]{1, 1});

        Neuron neuron = neuralNetwork.getOutputLayer().get(0);

        double errorGradient = neuron.computeErrorGradientOutputLayer(0);

        assertEquals(-0.1274, Trainer.round(errorGradient));
    }

    @Test
    public void computeErrorGradientHiddenLayerTest() {
        neuralNetwork.computeOutput(new double[]{1, 1});

        Neuron lastNeuron = neuralNetwork.getOutputLayer().get(0);
        lastNeuron.computeErrorGradientOutputLayer(0);

        Neuron neuron = neuralNetwork.getHiddenLayer().get(0);

        double errorGradient = neuron.computeErrorGradientHiddenLayer();

        assertEquals(0.0381, Trainer.round(errorGradient));
    }
}
