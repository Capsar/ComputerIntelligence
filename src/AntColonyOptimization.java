import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.*;

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
     *
     * @param maze          the maze .
     * @param threads       the amount of threads used for the calculation.
     * @param antsPerThread the amount of ants that will be run per thread, antsPerGen = threads * antsPerThread
     * @param generations   the amount of generations.
     * @param Q             normalization factor for the amount of dropped pheromone
     * @param evaporation   the evaporation factor.
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
     * Driver function for Assignment 1
     */
    public static void main(String[] args) throws FileNotFoundException {
        //parameters
        int threads = 8;
        int antsPerThread = 6;
        int noGen = 3;
        double Q = 1000;
        double evaporate = 0.2;

        //construct the optimization objects
        Maze maze = Maze.createMaze("./data/hard maze.txt");
        PathSpecification spec = PathSpecification.readCoordinates("./data/hard coordinates.txt");
        AntColonyOptimization aco = new AntColonyOptimization(maze, threads, antsPerThread, noGen, Q, evaporate);

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

    /**
     * Loop that starts the shortest path process
     *
     * @param spec Spefication of the route we wish to optimize
     * @return ACO optimized route
     */
    public Route findShortestRoute(PathSpecification spec) {
        maze.reset();

        ArrayList<Route> routes = new ArrayList<>();
        ArrayList<Route> allRoutes = new ArrayList<>();
        ArrayList<Future> routeFutures = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(threads);

        for (int gen = 0; gen < generations; gen++) {

            for (int i = 0; i < threads; i++)
                routeFutures.add(es.submit(new FindRouteThread(antsPerThread, maze, spec, Q)));

            routeFutures.forEach(future -> {
                while (!future.isDone()) ;
                try {
                    ArrayList<Route> futureRoutes = (ArrayList<Route>) future.get();
                    routes.addAll(futureRoutes);
                    } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            maze.resetLocalPheromones();

            Route fastestRouteOfGeneration = routes.get(0);
            for (Route r : routes)
                if (r.shorterThan(fastestRouteOfGeneration))
                    fastestRouteOfGeneration = r;

            maze.evaporate(evaporation);
            maze.addGlobalPheromoneRoute(fastestRouteOfGeneration, Q);

            allRoutes.addAll(routes);
            System.out.println("Finished generation: " + gen + " with " + routes.size() + " new routes, the fastest: " + fastestRouteOfGeneration.size());
            routeFutures.clear();
            routes.clear();
        }

        es.shutdown();

        Route fastestRoute = allRoutes.get(0);
        for (Route r : allRoutes) {
            if (r.shorterThan(fastestRoute))
                fastestRoute = r;
        }

        Ant2 ant = new Ant2(maze, spec);
        Route lastAntRoute = ant.findRoute();

        maze.createPheromoneFile();

        System.out.println("finished all gens with fastestRoute=" + fastestRoute.size() + " lastAntRoute=" + lastAntRoute.size() + " from " + spec.getStart() + " to " + spec.getEnd());

        return fastestRoute;
    }
}
