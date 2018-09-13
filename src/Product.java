/**
 * Products class that contains a single training product
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Product {
    private double[] features;
    private int target;

    /**
     * Constructor for the product object.
     * @param features the features of the products
     * @param target the target group that the product should be located in
     */
    public Product(double[] features, int target) {
        this.features = features;
        this.target = target;
    }

    /**
     * Getter for the features
     * @return list of features
     */
    public double[] getFeatures() {
        return features;
    }

    /**
     * Getter for the target
     * @return target
     */
    public int getTarget() {
        return target;
    }
}
