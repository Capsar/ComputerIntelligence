/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {
    /**
     * https://hastebin.com/aserejuhev.css
     * @param args
     */
    public static void main(String[] args) {
        for(int hidden = 14; hidden < 22; hidden++) {
            System.out.print("\nH; " + hidden + " ");
            int averageErrors = 0;
            int trainingCycles = 40;
            for(double lr = 0.4; lr <= 0.7; lr += 0.1) {
                for (int trainings = 0; trainings < trainingCycles; trainings++) {
                    Trainer trainer = new Trainer(new NeuralNetwork(10, hidden, 7, lr), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 0.000001);
                    int errors = trainer.trainNetwork();
                    averageErrors += errors;
                }
                averageErrors /= trainingCycles;
                System.out.print("[LR; " + lr + " AE; " + averageErrors + "]  ");
            }
        }
    }
}
