import java.util.*;

/**
 * TEST CLASS SHOULD NOT BE USED IN PROGRAM, IS VERY INEFFICIENT!!
 *
 * Class that represents the ants functionality.
 */
public class Ant3 {

    private static Random rand;
    private Maze maze;
    private Coordinate start;
    private Coordinate end;
    private Coordinate currentPosition;
    private Route route;
    private Stack<Coordinate> visitedCoordinates;
    private WeightedCollection weightedPossibleDirections;
    private Coordinate previousPosition;

    /**
     * Constructor for ant taking a Maze and PathSpecification.
     *
     * @param maze Maze the ant will be running in.
     * @param spec The path specification consisting of a start coordinate and an end coordinate.
     */
    public Ant3(Maze maze, PathSpecification spec) {
        this.maze = maze;
        this.start = spec.getStart();
        this.end = spec.getEnd();
        this.currentPosition = start;
        route = new Route(this.currentPosition);
        if (rand == null) {
            rand = new Random();
        }
        weightedPossibleDirections = new WeightedCollection();
        visitedCoordinates = new Stack<>();
    }

    /**
     * Method that performs a single run through the maze by the ant.
     *
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        if (start.equals(end)) return route;
        previousPosition = currentPosition;
        Direction dir = randomDirection();
        takeStep(dir);
        long begin = System.currentTimeMillis();
        while (!currentPosition.equals(end)) {
            List<Direction> possibleDirections = getPossibleDirections();
            updateDirectionProbabilities(possibleDirections);
            dir = weightedPossibleDirections.get();
            takeStep(dir);
        }
        System.out.println("Found route.");

        return simplifyRoute(route);
    }

    private Route simplifyRoute(Route route) {
        currentPosition = route.getStart();
        visitedCoordinates.push(currentPosition);
        List<Direction> routeList = route.getRoute();
        System.out.println("Route size at first: " + routeList.size());
        int index = 0;
        int wat = 0;
        while (!currentPosition.equals(end)) {
            currentPosition = currentPosition.add(routeList.get(index));
            boolean beenHere = contains(visitedCoordinates, currentPosition);
            visitedCoordinates.push(currentPosition);
            if (beenHere) {
                Coordinate beenHereCoordinate = currentPosition.add(new Coordinate(0,0));
                boolean first = true;
                while (first || !currentPosition.equals(beenHereCoordinate)) {
                    Coordinate pop = visitedCoordinates.pop();
                    Direction dir = routeList.remove(index);
                    index--;
                    currentPosition = currentPosition.subtract(dir);
                    first = false;
                }
            }
            index++;
            wat++;
            if(wat % 1000 == 0)
                System.out.println("Wat: " + wat + "  Index: " + index + "  RouteSize: " + routeList.size());
        }

        Route newRoute = new Route(start);
        for (Direction dir : routeList)
            newRoute.add(dir);

        System.out.println("Route size after simplify: " + newRoute.size());
        return newRoute;
    }

    private void takeStep(Direction dir) {
        previousPosition = currentPosition;
        currentPosition = currentPosition.add(dir);
        route.add(dir);
//        System.out.println("f: " + currentPosition);
    }

    private Direction randomDirection() {
        ArrayList<Direction> possibleDirections = getPossibleDirections();
        Collections.shuffle(possibleDirections);
        return possibleDirections.get(0);
    }

    private ArrayList<Direction> getPossibleDirections() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.intToDir(i);
            Coordinate next = currentPosition.add(direction);
            // Check if the direction is to take results in a path
            if (maze.isPath(next)) {
                possibleDirections.add(direction);
            }
        }

        return possibleDirections;
    }

    private boolean contains(Stack<Coordinate> stack, Coordinate pos) {
        for (Coordinate coordinate : stack) {
            if (coordinate.equals(pos))
                return true;
        }
        return false;
    }


    /**
     * Update the list of weighted probabilities by adding all the possible directions with their relative probabilities.
     *
     * @param possibleDirections the directions that are possible to take
     */
    public void updateDirectionProbabilities(List<Direction> possibleDirections) {
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition, possibleDirections);

        // Reset the possible directions and add the new directions with their probabilities
        weightedPossibleDirections.reset();

        // Get the total amount of pheromone for the possible directions (so excluding the direction where the ant came from)
        double totalPheromone = 0;
        double[] pheromones = new double[possibleDirections.size()];
        for (int i = 0; i < possibleDirections.size(); i++) {
            Direction dir = possibleDirections.get(i);
            pheromones[i] = surroundingPheromone.get(dir);
            //If there is no pheromone we still sometimes want to walk that path, so we give it a none 0 chance.
            if (pheromones[i] == 0)
                pheromones[i] = 1;
            totalPheromone += pheromones[i];
        }

        for (int i = 0; i < possibleDirections.size(); i++) {
            Direction dir = possibleDirections.get(i);
            double pheromone = pheromones[i];
            double probability = pheromone / totalPheromone;
            weightedPossibleDirections.add(dir, probability);
        }
    }
}

