import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Class containing the product distances. Can be either build from a maze, a product
 * location list and a PathSpecification or be reloaded from a file.
 */
public class TSPData implements Serializable {
    private static final long serialVersionUID = 0L;

    private PathSpecification spec;
    private ArrayList<Coordinate> productLocations;

    private int[][] distances;
    private int[] startDistances;
    private int[] endDistances;
    private Route[][] productToProduct;
    private Route[] startToProduct;
    private Route[] productToEnd;

    /**
     * Constructs a new TSP data object.
     * @param productLocations the productlocations.
     * @param spec the path specification.
     */
    private TSPData(ArrayList<Coordinate> productLocations, PathSpecification spec) {
        this.productLocations = productLocations;
        this.spec = spec;
    }

    /**
     * Calculate the routes from the product locations to each other, the start, and the end.
     * Additionally generate arrays that contain the length of all the routes.
     * @param maze
     */
    public void calculateRoutes(AntColonyOptimization aco) {
        productToProduct = buildDistanceMatrix(aco);
        startToProduct = buildStartToProducts(aco);
        productToEnd = buildProductsToEnd(aco);
        buildDistanceLists();
    }

    /**
     * Build a list of integer distances of all the product-product routes.
     */
    private void buildDistanceLists() {
        int numberOfProducts = productLocations.size();
        distances = new int[numberOfProducts][numberOfProducts];
        startDistances = new int[numberOfProducts];
        endDistances = new int[numberOfProducts];
        for (int i = 0; i < numberOfProducts; i++) {
            for (int j = 0; j < numberOfProducts; j++) {
                distances[i][j] = productToProduct[i][j].size();
            }
            startDistances[i] = startToProduct[i].size();
            endDistances[i] = productToEnd[i].size();
        }
    }

    /**
     * Distance product to product getter
     * @return the list
     */
    public int[][] getDistances() {
        return distances;
    }

    /**
     * Distance start to product getter
     * @return the list
     */
    public int[] getStartDistances() {
        return startDistances;
    }

    /**
     * Distance product to end getter
     * @return the list
     */
    public int[] getEndDistances() {
        return endDistances;
    }


    /**
     * Equals method
     * @param other other TSPData to check
     * @return boolean whether equal
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TSPData)) {
            return false;
        } else {
            TSPData otherPD = (TSPData) other;
            return Arrays.deepEquals(this.distances, otherPD.distances)
                    && Arrays.deepEquals(this.productToProduct, otherPD.productToProduct)
                    && Arrays.deepEquals(this.productToEnd, otherPD.productToEnd)
                    && Arrays.deepEquals(this.startToProduct, otherPD.startToProduct)
                    && this.spec.equals(otherPD.spec)
                    && this.productLocations.equals(otherPD.productLocations);
        }
    }

    /**
     * Persist object to file so that it can be reused later
     * @param filePath Path to persist to
     */
    public void writeToFile(String filePath) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                new FileOutputStream(filePath));
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
    }

    /**
     * Write away an action file based on a solution from the TSP problem.
     * @param productOrder Solution of the TSP problem
     * @param filePath Path to the solution file
     */
    public void writeActionFile(int[] productOrder, String filePath) throws FileNotFoundException {
        int totalLength = startDistances[productOrder[0]];
        for (int i = 0; i < productOrder.length - 1; i++) {
            int from = productOrder[i];
            int to = productOrder[i+1];
            totalLength += distances[from][to];
        }
        totalLength += endDistances[productOrder[productOrder.length - 1]] + productOrder.length;

        StringBuilder sb = new StringBuilder();
        sb.append(totalLength).append(";\n");
        sb.append(spec.getStart()).append(";\n");
        sb.append(startToProduct[productOrder[0]]);
        sb.append("take product #").append(productOrder[0] + 1).append(";\n");
        for (int i = 0; i < productOrder.length - 1; i++) {
            int from = productOrder[i];
            int to = productOrder[i+1];
            sb.append(productToProduct[from][to]);
            sb.append("take product #").append(to + 1).append(";\n");
        }
        sb.append(productToEnd[productOrder[productOrder.length - 1]]);

        PrintWriter pw = new PrintWriter(filePath);
        pw.write(sb.toString());
        pw.close();
    }


    /**
     * Calculate the optimal routes between all the individual routes
     * @param maze Maze to calculate optimal routes in
     * @return Optimal routes between all products in 2d array
     */
    private Route[][] buildDistanceMatrix(AntColonyOptimization aco) {
        int numberOfProduct = productLocations.size();
        Route[][] productToProduct = new Route[numberOfProduct][numberOfProduct];
        for (int i = 0; i < numberOfProduct; i++) {
            for (int j = 0; j < numberOfProduct; j++) {
                Coordinate start = productLocations.get(i);
                Coordinate end = productLocations.get(j);
                productToProduct[i][j] = aco.findShortestRoute(new PathSpecification(start, end));
            }
        }
        return productToProduct;
    }

    /**
     * Calculate optimal route between the start and all the products
     * @param maze Maze to calculate optimal routes in
     * @return Optimal route from start to products
     */
    private Route[] buildStartToProducts(AntColonyOptimization aco) {
        Coordinate start = spec.getStart();
        Route[] startToProducts = new Route[productLocations.size()];
        for (int i = 0; i < productLocations.size(); i++) {
            startToProducts[i] = aco.findShortestRoute(new PathSpecification(start, productLocations.get(i)));
        }
        return startToProducts;
    }

    /**
     * Calculate optimal routes between the products and the end point
     * @param maze Maze to calculate optimal routes in
     * @return Optimal route from products to end
     */
    private Route[] buildProductsToEnd(AntColonyOptimization aco) {
        Coordinate end = spec.getEnd();
        Route[] productsToEnd = new Route[productLocations.size()];
        for (int i = 0; i < productLocations.size(); i++) {
            productsToEnd[i] = aco.findShortestRoute(new PathSpecification(productLocations.get(i), end));
        }
        return productsToEnd;
    }

    /**
     * Load TSP data from a file
     * @param filePath Persist file
     * @return TSPData object from the file
     */
    public static TSPData readFromFile(String filePath) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(
                new FileInputStream(filePath));
        return (TSPData) objectInputStream.readObject();
    }

    /**
     * Read a TSP problem specification based on a coordinate file and a product file
     * @param coordinates Path to the coordinate file
     * @param productFile Path to the product file
     * @return TSP object with uninitiatilized routes
     */
    public static TSPData readSpecification(String coordinates, String productFile) throws FileNotFoundException {
        Scanner scan = new Scanner(new FileReader(productFile));
        ArrayList<Coordinate> productLocations = new ArrayList<>();
        scan.useDelimiter(Pattern.compile("[:,;]\\s*"));
        int numberOfProducts = scan.nextInt();
        for (int i = 0; i < numberOfProducts; i++) {
            int product = scan.nextInt();
            int x = scan.nextInt();
            int y = scan.nextInt();
            productLocations.add(new Coordinate(x, y));
        }
        PathSpecification spec = PathSpecification.readCoordinates(coordinates);

        return new TSPData(productLocations, spec);
    }

    /**
     * Assignment 2.a
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        
    	//parameters
    	int gen = 1;
        int noGen = 1;
        double Q = 1000;
        double evap = 0.1;
        String persistFile = "./tmp/productMatrixDist";
        String TSPpath = "./data/tsp products.txt";
        String coordinates = "./data/hard coordinates.txt";
        
        //construct optimization
        Maze maze = Maze.createMaze("./data/hard maze.txt");
        TSPData pd = TSPData.readSpecification(coordinates, TSPpath);
        AntColonyOptimization aco = new AntColonyOptimization(maze, gen, noGen, Q, evap);
        
        //run optimization and write to file
        pd.calculateRoutes(aco);
        pd.writeToFile(persistFile);
        
        //read from file and print
        TSPData pd2 = TSPData.readFromFile(persistFile);
        System.out.println(pd.equals(pd2));
    }
}
