import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.*;

/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {

    public static void main(String[] args) {
//        Trainer trainer = new Trainer(new NeuralNetwork(10, 16, 7, 0.01, -2, 2, -2, 2), Trainer.loadTrainingSet("src/files/features.txt", "src/files/targets.txt"), 5);
        //trainer.trainKFoldNetwork();
        //trainer.createOutputFile("src/files/unknown.txt", "classes.txt");

//        trainer.createParameterFile(new TrainParameters(7, 30, 0.1,  1.0, 0.1, new double[] {0, 1}, new double[] {0, 1}, new double[] {0, 1}, new double[] {0, 1}, 0.1), 8);

        ArrayList<Future> resultFutures = new ArrayList<>();
        ArrayList<NetworkResult> results = new ArrayList<NetworkResult>();
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        System.out.println("Start training 10 times");

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            resultFutures.add(executorService.submit(new Callable<ArrayList<NetworkResult>>() {
                @Override
                public ArrayList<NetworkResult> call() throws Exception {
                    Trainer trainer = new Trainer(new NeuralNetwork(10, 16, 7, 0.1, -2, 2, -2, 2), Trainer.loadTrainingSet("src/files/features.txt", "src/files/targets.txt"), 5);
                    trainer.trainKFoldNetwork();
                    System.out.println("run=" + finalI);
                    return trainer.getNetworkResults();
                }

                ;
            }));
        }
        for (Future f : resultFutures) {
            try {
                results.addAll((Collection<? extends NetworkResult>) f.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        boolean finished = false;
        while (!finished) {
            finished = true;
            for (int i = 0; i < resultFutures.size(); i++)
                if (!resultFutures.get(i).isDone())
                    finished = false;


        }


        results.sort(new Comparator<NetworkResult>() {
            @Override
            public int compare(NetworkResult o1, NetworkResult o2) {
                float averagePer1 = (o1.getPercentageTest() + o1.getPercentageVali()) / 2;
                float averagePer2 = (o2.getPercentageTest() + o2.getPercentageVali()) / 2;
                return (averagePer1 <= averagePer2) ? -1 : 1;
            }
        });
        for (NetworkResult result : results) {
            float averagePer1 = ((result.getPercentageTest() + result.getPercentageVali()) / 2)*1000;
            System.out.println(Trainer.round(averagePer1));
        }

        NetworkResult result = results.get(0);
        NeuralNetwork network = result.getNetwork();
        System.out.println("Network results:");
        System.out.println(" -V- MSE=" + result.getCheckValidation()[2] + "  -T- MSE=" + result.getCheckTest()[2]);
        System.out.println(" -V- Errors=" + result.getCheckValidation()[0] + "  -T- Errors=" + result.getCheckTest()[0]);
        System.out.println(" -V- Percentage=" + result.getCheckValidation()[1] + "  -T- Percentage=" + result.getCheckTest()[1]);

        network.print();
        Trainer trainer = new Trainer(network, result.getTrainingSet(), 5);
        float[] check = trainer.checkKFoldNetwork(0);
        System.out.println("check: " + check[0] + ", " + check[1] + ", " + check[2]);
        trainer.createOutputFile("src/files/unknown.txt", "classes2.txt");




//        TrainParameters parameters = new TrainParameters(14, 21, 0.05, 0.2, 0.05, new double[]{-3.0, -0.5}, new double[]{0.5, 3.0}, new double[]{-3.0, -0.5}, new double[]{0.5, 3.0}, 0.1);
//        trainer.createParameterFile(parameters, 8);
    }
}