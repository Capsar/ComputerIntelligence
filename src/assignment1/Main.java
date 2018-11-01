package assignment1;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {

    public static final String DATA = "/resources/assignment1/";

    public static void main(String[] args) {

        NetworkResult result;
        try {
            result = readFromFile("./tmp/networkResult");
        } catch (Exception readFileException) {
            readFileException.printStackTrace();


            ArrayList<Future> resultFutures = new ArrayList<>();
            ArrayList<NetworkResult> results = new ArrayList<>();
            ExecutorService es = Executors.newFixedThreadPool(8);
            //Number of times to train the network
            int tests = 50;
            System.out.println("Start training " + tests + " times");

            //Add all the futures in the futures list & execute the threads.
            for (int i = 0; i < tests; i++) {
                resultFutures.add(es.submit(() -> {
                    Trainer trainer = new Trainer(new NeuralNetwork(10, 22, 7, 0.1, -2, 2, -2, 2), Trainer.loadTrainingSet(DATA + "features.txt", DATA + "targets.txt"), 5);
                    trainer.trainKFoldNetwork();
                    return trainer.getNetworkResults();
                }));
            }

            resultFutures.forEach(future -> {
                while (!future.isDone())

                    try {
                        results.addAll((Collection<? extends NetworkResult>) future.get());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
            es.shutdown();


            //Sort the results list from best to worst results, using the average failure rate of the validation and test set.
            results.sort((o1, o2) -> {
                float averagePer1 = (o1.getPercentageTest() + o1.getPercentageValidation()) / 2;
                float averagePer2 = (o2.getPercentageTest() + o2.getPercentageValidation()) / 2;
                return (averagePer1 <= averagePer2) ? -1 : 1;
            });


            //Print the results of the most accurate network.
            result = results.get(0);
            System.out.println("Network results:");
            System.out.println(" -V- MSE=" + result.getCheckValidation()[2] + "  -T- MSE=" + result.getCheckTest()[2]);
            System.out.println(" -V- Errors=" + result.getCheckValidation()[0] + "  -T- Errors=" + result.getCheckTest()[0]);
            System.out.println(" -V- Percentage=" + result.getCheckValidation()[1] + "  -T- Percentage=" + result.getCheckTest()[1]);
        }


        Trainer trainer = new Trainer(result.getNetwork(), result.getTrainingSet(), 5);
        float[] check = trainer.checkKFoldNetwork(0);
        System.out.println("\ncheck: " + check[0] + ", " + check[1] + ", " + check[2]);

        try {
            writeToFile(result, "./tmp/trainerNetwork");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Print the classifications of the unknown features into classes.txt
        trainer.createOutputFile(DATA + "unknown.txt", "./outputFiles/classes.txt");

    }

    /**
     * Rounds a number to variable decimal places.
     *
     * @param number the number to be rounded
     * @return
     */
    public static double round(double number, int decimal) {
        BigDecimal bigDecimal = new BigDecimal(number);
        bigDecimal = bigDecimal.setScale(decimal, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }


    /**
     * Persist object to file so that it can be reused later
     *
     * @param filePath Path to persist to
     */
    public static void writeToFile(NetworkResult networkResult, String filePath) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(filePath));
        objectOutputStream.writeObject(networkResult);
        objectOutputStream.close();
    }

    /**
     * Load TSP data from a file
     *
     * @param filePath Persist file
     * @return TSPData object from the file
     */
    public static NetworkResult readFromFile(String filePath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(filePath));
        return (NetworkResult) objectInputStream.readObject();
    }

}