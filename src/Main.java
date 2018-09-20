/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {

            Trainer trainer = new Trainer(new NeuralNetwork(10, 18, 7, 0.1), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 5);
            trainer.trainKFoldNetwork();
//        trainer.trainNetwork(100);
//        System.out.println(trainer.checkNetwork());

//        for(int hidden = 1; hidden < 22; hidden++) {
//            System.out.print("\nH; " + hidden + " ");
//            int trainingCycles = 2;
//            double lr = 0.15;
//            for(; lr <= 0.6; lr += 0.1) {
//                int totalIC = 0;
//                System.out.print("[LR; " + lr + " [");
//                for (int trainings = 0; trainings < trainingCycles; trainings++) {
//                    Trainer trainer = new Trainer(new NeuralNetwork(10, hidden, 7, lr), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 0.000001);
//                    trainer.trainNetwork(80);
//                    int incorrectClassifications = trainer.checkNetwork();
//                    System.out.print("E; " + incorrectClassifications + " ");
//                    totalIC += incorrectClassifications;
//                }
//                totalIC /= trainingCycles;
//                System.out.print("] AE; " + totalIC +"]  ");
//            }
//
//        }
    }
}
