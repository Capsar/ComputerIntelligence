import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {


    public static void main(String[] args) {
        for (double i = -1.0; i <= 1.0; i += 0.1) {
            for (double j = -3.0; j <= 1.0; j += 0.2) {
                double finalI = round(i, 1);
                double finalJ = round(j, 1);
                Trainer trainer = new Trainer(new NeuralNetwork(10, 16, 7, 0.25, finalI, finalI + 0.05, finalJ, finalJ + 0.05), Trainer.loadTrainingSet("src/files/features.txt", "src/files/targets.txt"), 5);
                trainer.trainKFoldNetwork();
                System.out.println(finalI + ", " + finalJ + ", " + trainer.computeMSE(0) + ", 0.0, 0.0, 0.0, 0.0, 0.0");
            }
        }
//
//        ArrayList<Future> resultFutures = new ArrayList<>();
//        ArrayList<NetworkResult> results = new ArrayList<NetworkResult>();
//        ExecutorService executorService = Executors.newFixedThreadPool(8);
//        //Number of times to train the network
//        int tests = 20;
//        System.out.println("Start training " + tests + " times");
//
//        //Add all the futures in the futures list & execute the threads.
//        for (int i = 0; i < tests; i++) {
//            double finalI = i;
//            resultFutures.add(executorService.submit(new Callable<ArrayList<NetworkResult>>() {
//                @Override
//                public ArrayList<NetworkResult> call() throws Exception {
//                    Trainer trainer = new Trainer(new NeuralNetwork(10, 22, 7, 0.1, -2, 2, -2, 2), Trainer.loadTrainingSet("src/files/features.txt", "src/files/targets.txt"), 5);
//                    trainer.trainKFoldNetwork();
//                    return trainer.getNetworkResults();
//                }
//
//                ;
//            }));
//        }
//        //Add all the results into the results arraylist.
//        for (Future f : resultFutures) {
//            try {
//                results.addAll((Collection<? extends NetworkResult>) f.get());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        //Check whether the threads are all finished, if so continue.
//        boolean finished = false;
//        while (!finished) {
//            finished = true;
//            for (int i = 0; i < resultFutures.size(); i++)
//                if (!resultFutures.get(i).isDone())
//                    finished = false;
//
//
//        }
//
//
//        //Sort the results list from best to worst results, using the average failure rate of the validation and test set.
//        results.sort(new Comparator<NetworkResult>() {
//            @Override
//            public int compare(NetworkResult o1, NetworkResult o2) {
//                float averagePer1 = (o1.getPercentageTest() + o1.getPercentageVali()) / 2;
//                float averagePer2 = (o2.getPercentageTest() + o2.getPercentageVali()) / 2;
//                return (averagePer1 <= averagePer2) ? -1 : 1;
//            }
//        });
//        //Print the percentages.
//        for (NetworkResult result : results) {
//            float averagePer1 = ((result.getPercentageTest() + result.getPercentageVali()) / 2)*1000;
//            System.out.println(Trainer.round(averagePer1));
//        }
//
//        //Print the results of the most accurate network.
//        NetworkResult result = results.get(0);
//        NeuralNetwork network = result.getNetwork();
//        System.out.println("Network results:");
//        System.out.println(" -V- MSE=" + result.getCheckValidation()[2] + "  -T- MSE=" + result.getCheckTest()[2]);
//        System.out.println(" -V- Errors=" + result.getCheckValidation()[0] + "  -T- Errors=" + result.getCheckTest()[0]);
//        System.out.println(" -V- Percentage=" + result.getCheckValidation()[1] + "  -T- Percentage=" + result.getCheckTest()[1]);
//
//        //Print al of the thresholds and weights of the network.
//        //network.print();
//        Trainer trainer = new Trainer(network, result.getTrainingSet(), 5);
//        float[] check = trainer.checkKFoldNetwork(0);
//        System.out.println("\ncheck: " + check[0] + ", " + check[1] + ", " + check[2]);
//
//        int[][] confusion = trainer.checkConfusionMatrix(0);
//        for(int i = 1; i < confusion.length; i++) {
//            for(int j = 1; j < confusion[i].length; j++) {
//                System.out.println("current=" + i + " true=" + j + " amount=" + confusion[i][j]);
//            }
//        }
//
//        //Print the classifications of the unknown features into classes3.txt
//        trainer.createOutputFile("src/files/unknown.txt", "classes3.txt");


//        TrainParameters parameters = new TrainParameters(14, 21, 0.05, 0.2, 0.05, new double[]{-3.0, -0.5}, new double[]{0.5, 3.0}, new double[]{-3.0, -0.5}, new double[]{0.5, 3.0}, 0.1);
//        trainer.createParameterFile(parameters, 8);
    }

    /**
     * Rounds a number to 4 decimal places.
     *
     * @param number the number to be rounded
     * @return
     */
    public static double round(double number, int decimal) {
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(decimal, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }
}