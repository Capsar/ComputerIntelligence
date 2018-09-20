/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {
    public static void main(String[] args) {
        Trainer trainer = new Trainer(new NeuralNetwork(10, 27, 7, 0.9, -0.5, 0.5, -0.5, 0.5), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 0.001);

        TrainParameters parameters = new TrainParameters(7, 20, 0.1, 1.0, 0.1, new double[]{-0.5, 0.5}, new double[]{-0.5, 0.5}, new double[]{-0.5, 0.5}, new double[]{-0.5, 0.5}, 0.5);
        trainer.createParameterFile(parameters, 2, 8);
        }
    }