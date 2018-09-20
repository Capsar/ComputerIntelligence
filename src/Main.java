/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        Trainer trainer = new Trainer(new NeuralNetwork(10, 27, 7, 0.1, -0.5, 0.5, -0.5, 0.5), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 6);
        trainer.trainKFoldNetwork();

        //System.out.println(trainer.trainNetwork(10));

        TrainParameters parameters = new TrainParameters(7, 30, 0.1, 1.0, 0.1, new double[]{-0.5, 0}, new double[]{0, 0.5}, new double[]{-0.5, 0}, new double[]{0, 0.5}, 0.1);

        trainer.findBestParameters(parameters, 2);
    }
}
