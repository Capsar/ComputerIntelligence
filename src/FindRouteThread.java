import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created by Sam van Berkel on 24/10/2018.
 */
public class FindRouteThread implements Callable<ArrayList<Route>> {
    private int antsPerThread;
    private Maze maze;
    private PathSpecification pathSpecification;
    private double Q;

    public FindRouteThread(int antsPerThread, Maze maze, PathSpecification pathSpecification, double q) {
        this.antsPerThread = antsPerThread;
        this.maze = maze;
        this.pathSpecification = pathSpecification;
        Q = q;
    }

    @Override
    public ArrayList<Route> call() throws Exception {
        ArrayList<Route> routes = new ArrayList<>();

        for (int i = 0; i < antsPerThread; i++) {
            Ant2 ant = new Ant2(maze, pathSpecification);
            Route route = ant.findRoute();
            maze.addLocalPheromoneRoute(route, Q);
            routes.add(route);
        }

        return routes;
    }
}
