import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.*;

/**
 * Created by Sam van Berkel on 18/09/2018.
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        ArrayList<Future> resultFutures = new ArrayList<>();
        ArrayList<NetworkResult> results = new ArrayList<NetworkResult>();
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        for (int i = 0; i < 100; i++) {
            resultFutures.add(executorService.submit(new Callable<ArrayList<NetworkResult>>() {
                @Override
                public ArrayList<NetworkResult> call() throws Exception {
                    Trainer trainer = new Trainer(new NeuralNetwork(10, 16, 7, 0.05, -2, 2, -2, 2), Trainer.loadData("src/files/features.txt", "src/files/targets.txt"), 5);
                    trainer.trainKFoldNetwork();
//                    System.out.println("");
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


//        TrainParameters parameters = new TrainParameters(14, 21, 0.05, 0.2, 0.05, new double[]{-3.0, -0.5}, new double[]{0.5, 3.0}, new double[]{-3.0, -0.5}, new double[]{0.5, 3.0}, 0.1);
//        trainer.createParameterFile(parameters, 8);
    }
}