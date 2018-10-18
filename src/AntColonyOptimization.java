import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Class representing the first assignment. Finds shortest path between two points in a maze according to a specific
 * path specification.
 */
public class AntColonyOptimization {
	
	private int antsPerGen;
    private int generations;
    private double Q;
    private double evaporation;
    private Maze maze;

    /**
     * Constructs a new optimization object using ants.
     * @param maze the maze .
     * @param antsPerGen the amount of ants per generation.
     * @param generations the amount of generations.
     * @param Q normalization factor for the amount of dropped pheromone
     * @param evaporation the evaporation factor.
     */
    public AntColonyOptimization(Maze maze, int antsPerGen, int generations, double Q, double evaporation) {
        this.maze = maze;
        this.antsPerGen = antsPerGen;
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

        for (int gen = 0; gen < generations; gen++) {
            System.out.println("starting gen: " + gen);
            for (int i = 0; i < antsPerGen; i++) {
                System.out.println("starting ant " + i);
                Ant currentAnt = new Ant(maze, spec);
                Route route = currentAnt.findRoute();
                routes.add(route);
            }

            maze.evaporate(evaporation);

            maze.addPheromoneRoutes(routes, Q);
        }

        System.out.println("finished all gens");

        Route fastestRoute = routes.get(0);

        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).shorterThan(fastestRoute)) {
                fastestRoute = routes.get(i);
            }
        }

        return fastestRoute;
    }

    /**
     * Driver function for Assignment 1
     */
    public static void main(String[] args) throws FileNotFoundException {
    	//parameters
    	int ants = 10;
        int noGen = 100;
        double Q = 1600;
        double evap = 0.1;
        
        //construct the optimization objects
        Maze maze = Maze.createMaze("./data/medium maze.txt");
        PathSpecification spec = PathSpecification.readCoordinates("./data/medium coordinates.txt");
        AntColonyOptimization aco = new AntColonyOptimization(maze, ants, noGen, Q, evap);
        
        //save starting time
        long startTime = System.currentTimeMillis();
        
        //run optimization
        Route shortestRoute = aco.findShortestRoute(spec);
        
        //print time taken
        System.out.println("Time taken: " + ((System.currentTimeMillis() - startTime) / 1000.0));
        
        //save solution
        shortestRoute.writeToFile("./data/medium_solution.txt");
        
        //print route size
        System.out.println("Route size: " + shortestRoute.size());
    }
}
