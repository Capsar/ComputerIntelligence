/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {

    public static void main(String[] args) {
        Trainer trainer = new Trainer(new NeuralNetwork(10, 27, 7, 0.1, -0.5, 0.5, -0.5, 0.5), Trainer.loadTrainData("src/files/features.txt", "src/files/targets.txt"), 6);
        trainer.trainKFoldNetwork();

        trainer.createOutputFile("src/files/features.txt", "classes.txt");

        //TrainParameters parameters = new TrainParameters(7, 10, 0.1, 0.1, 0.1, new double[]{0.0, 0.0}, new double[]{1, 1}, new double[]{0.0, 0.0}, new double[]{1, 1}, 1.0);
        //trainer.createParameterFile(parameters,  1);
    }
}