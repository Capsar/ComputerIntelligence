import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class representing the first assignment. Finds shortest path between two points in a maze according to a specific
 * path specification.
 */
public class AntColonyOptimization {

    private int threads;
	private int antsPerThread;
    private int generations;
    private double Q;
    private double evaporation;
    private Maze maze;

    /**
     * Constructs a new optimization object using ants.
     * @param maze the maze .
     * @param threads the amount of threads used for the calculation.
     * @param antsPerThread the amount of ants that will be run per thread, antsPerGen = threads * antsPerThread
     * @param generations the amount of generations.
     * @param Q normalization factor for the amount of dropped pheromone
     * @param evaporation the evaporation factor.
     */
    public AntColonyOptimization(Maze maze, int threads, int antsPerThread, int generations, double Q, double evaporation) {
        this.maze = maze;
        this.threads = threads;
        this.antsPerThread = antsPerThread;
        this.generations = generations;
        this.Q = Q;
        this.evaporation = evaporation;
    }

    /**
     * Loop that starts the shortest path process
     * @param spec Spefication of the route we wish to optimize
     * @return ACO optimized route
     */
    public Route findShortestRoute(PathSpecification spec) {
        maze.reset();

        ArrayList<Route> routes = new ArrayList<>();
        ArrayList<Future> routeFutures = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(threads);

        for (int gen = 0; gen < generations; gen++) {
            routes  = new ArrayList<>();
            System.out.println("starting gen: " + gen);
            for (int i = 0; i < threads; i++) {
                routeFutures.add(es.submit(new FindRouteThread(antsPerThread, maze, spec)));
            }

            boolean finished = false;

            // Check if every thread has finished
            while (!finished) {
                finished = true;
                for (int i = 0; i < routeFutures.size(); i++) {
                    if (!routeFutures.get(i).isDone()) {
                        finished = false;
                    }
                }
            }

            // Add the results of every thread to a single list
            for (int i = 0; i < routeFutures.size(); i++) {
                ArrayList<Route> currentRoutes = null;
                try {

                    if (routeFutures.get(i).isDone()) {
                        routeFutures.get(i).get();
                        currentRoutes = (ArrayList<Route>) routeFutures.get(i).get();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                routes.addAll(currentRoutes);
            }

            maze.evaporate(evaporation);

            maze.addPheromoneRoutes(routes, Q);
        }

        es.shutdown();

        // Find the fastest route in the list of routes
        Route fastestRoute = routes.get(0);
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).shorterThan(fastestRoute)) {
                fastestRoute = routes.get(i);
            }
        }

        maze.createPheromoneFile();

        System.out.println("finished all gens with fastest route of length: " + fastestRoute.size());

        return fastestRoute;
    }

    /**
     * Driver function for Assignment 1
     */
    public static void main(String[] args) throws FileNotFoundException {
    	//parameters
    	int threads = 8;
    	int antsPerThread = 2;
        int noGen = 1;
        double Q = 1600;
        double evap = 0.1;
        
        //construct the optimization objects
        Maze maze = Maze.createMaze("./data/hard maze.txt");
        PathSpecification spec = PathSpecification.readCoordinates("./data/hard coordinates.txt");
        AntColonyOptimization aco = new AntColonyOptimization(maze, threads, antsPerThread, noGen, Q, evap);
        
        //save starting time
        long startTime = System.currentTimeMillis();
        
        //run optimization
        Route shortestRoute = aco.findShortestRoute(spec);
        
        //print time taken
        System.out.println("Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0));
        
        //save solution
        shortestRoute.writeToFile("./data/hard_solution.txt");
        
        //print route size
        System.out.println("Route size: " + shortestRoute.size());
    }
}
