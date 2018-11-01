package assignment3;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class representing the first assignment. Finds shortest path between two points in a maze according to a specific
 * path specification.
 */
public class AntColonyOptimization {

    private int threads;
    private int numberOfAnts;
    private int generations;
    private double Q;
    private double evaporation;
    private Maze maze;

    /**
     * Constructs a new optimization object using ants.
     *
     * @param maze         the maze .
     * @param threads      the amount of threads used for the calculation.
     * @param numberOfAnts the amount of ants that will be run per thread, antsPerGen = threads * numberOfAnts
     * @param generations  the amount of generations.
     * @param Q            normalization factor for the amount of dropped pheromone
     * @param evaporation  the evaporation factor.
     */
    public AntColonyOptimization(Maze maze, int threads, int numberOfAnts, int generations, double Q, double evaporation) {
        this.maze = maze;
        this.threads = threads;
        this.numberOfAnts = numberOfAnts;
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
        int numberOfAnts = 100;
        int noGen = 100;
        double Q = 500;
        double evaporate = 0.1;

        //construct the optimization objects
        Maze maze = Maze.createMaze("/resources/assignment3/hard_maze.txt");
        PathSpecification spec = PathSpecification.readCoordinates("/resources/assignment3/hard_coordinates.txt");
        AntColonyOptimization aco = new AntColonyOptimization(maze, threads, numberOfAnts, noGen, Q, evaporate);

        //save starting time
        long startTime = System.currentTimeMillis();

        //run optimization
        Route shortestRoute = aco.findShortestRoute(spec);

        //print time taken
        System.out.println("Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0));

        //save solution
        shortestRoute.writeToFile("./outputFiles/75_hard.txt");

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
        ArrayList<Route> shortestRoutes = new ArrayList<>();
        ArrayList<Future> routeFutures = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(threads);

        for (int gen = 0; gen < generations; gen++) {

            for (int i = 0; i < numberOfAnts; i++)
                routeFutures.add(es.submit(() -> {
                    Ant ant = new Ant(maze, spec);
                    return ant.findRoute();
                }));

            routeFutures.forEach(future -> {
                while (!future.isDone()) ;
                try {
                    Route route = (Route) future.get();
                    routes.add(route);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Route shortestRouteOfGeneration = routes.get(0);
            for (Route r : routes) {
                if (r.shorterThan(shortestRouteOfGeneration))
                    shortestRouteOfGeneration = r;
            }

            maze.evaporate(evaporation);
            maze.addGlobalPheromoneRoute(shortestRouteOfGeneration, Q);

            shortestRoutes.add(shortestRouteOfGeneration);
//            System.out.println("Finished generation: " + gen + " with " + routes.size() + " new routes and the shortest: " + shortestRouteOfGeneration.size());
            routeFutures.clear();
            routes.clear();
        }

        es.shutdown();

        Route fastestRoute = shortestRoutes.get(0);
        for (Route r : shortestRoutes) {
            if (r.shorterThan(fastestRoute))
                fastestRoute = r;
        }

        Ant ant = new Ant(maze, spec);
        Route lastAntRoute = ant.findRoute();

        maze.createPheromoneFile();

//        System.out.println("finished all gens with fastestRoute=" + fastestRoute.size() + " lastAntRoute=" + lastAntRoute.size() + " from " + spec.getStart() + " to " + spec.getEnd());

        return fastestRoute;
    }
}
