/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {
    public static void main(String[] args) {
        Trainer trainer = new Trainer(new NeuralNetwork(10, 7, 7, 0.1), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 0.001);
        trainer.trainNetwork();
    }
}
