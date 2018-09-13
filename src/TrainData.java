import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Sam van Berkel on 12/09/2018.
 */
public class TrainData {

    /**
     * Creates a list of features and targets from two files and combines them into a list of products.
     * @param featuresUrl the url for the location of the features file
     * @param targetsUrl the url for the location of the targets file
     * @return list of generated products
     */
    public static ArrayList<Product> loadData(String featuresUrl, String targetsUrl) {
        ArrayList<Product> products = new ArrayList<>();

        try {
            // Create the readers for the features and targets files
            FileReader featuresFileReader = new FileReader(featuresUrl);
            FileReader targetsFileReader = new FileReader(targetsUrl);

            BufferedReader featuresReader = new BufferedReader(featuresFileReader);
            BufferedReader targetsReader = new BufferedReader(targetsFileReader);

            while(featuresReader.ready()) {
                // Get the current line of 10 features and convert them to an array of doubles
                String currentFeatures = featuresReader.readLine();
                String[] featureStrings = currentFeatures.split(",");
                double[] features = new double[featureStrings.length];

                for (int i = 0; i < features.length; i++) {
                    features[i] = Double.parseDouble(featureStrings[i]);
                }

                // Get the current target and convert it to an int
                int target = Integer.parseInt(targetsReader.readLine());

                // Create a product with the current features and targets and add it to the list
                products.add(new Product(features, target));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return products;
    }



}
