import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the Trainer class
 * Created by Sam van Berkel on 12/09/2018.
 */
public class TrainerTest {

    @Test
    public void loadDataTest() {
        ArrayList<TrainTarget> products = Trainer.loadData("src/files/features.txt", "src/files/targets.txt");
        assertEquals(products.size(), 7854);

        assertEquals(products.get(2).getDesiredOutputs()[1], 1.0);
        assertEquals(products.get(6524).getDesiredOutputs()[6], 1.0);

        assertEquals(products.get(2).getInputs()[4], 0.11558);
        assertEquals(products.get(6863).getInputs()[9], 0.23776);
    }

    @Test
    public void train() {
        ArrayList<TrainTarget> trainData = new ArrayList<>();
        trainData.add(new TrainTarget(new double[] {1, 1}, new double[]{0}));
        trainData.add(new TrainTarget(new double[] {0, 1}, new double[]{1}));
        trainData.add(new TrainTarget(new double[] {1, 0}, new double[]{1}));
        trainData.add(new TrainTarget(new double[] {0, 0}, new double[]{0}));

        NeuralNetwork neuralNetwork = new NeuralNetwork(2, 2, 1, 0.1);

        Trainer trainer = new Trainer(neuralNetwork, trainData, 0.001);

        trainer.trainNetwork();
    }


}
