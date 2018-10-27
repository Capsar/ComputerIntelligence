import java.util.*;

/**
 * Class that represents the ants functionality.
 */
public class Ant2 {

    private static Random rand;
    private final PathSpecification spec;
    private Maze maze;
    private Coordinate start;
    private Coordinate end;
    private Coordinate currentPosition;
    private Route route;
    private Stack<Coordinate> visitedCoordinates;
    private Stack<Coordinate> backtrackedCoordinates;
    private WeightedCollection weightedPossibleDirections;
    private Direction previousDirection;

    private int[][] positionCounter;

    /**
     * Constructor for ant taking a Maze and PathSpecification.
     *
     * @param maze Maze the ant will be running in.
     * @param spec The path specification consisting of a start coordinate and an end coordinate.
     */
    public Ant2(Maze maze, PathSpecification spec) {
        this.maze = maze;
        this.spec = spec;
        this.start = spec.getStart();
        this.end = spec.getEnd();
        this.currentPosition = start;
        route = new Route(this.currentPosition);
        if (rand == null) {
            rand = new Random();
        }
        positionCounter = new int[maze.getWidth()][maze.getLength()];
        weightedPossibleDirections = new WeightedCollection();
        visitedCoordinates = new Stack<>();
        backtrackedCoordinates = new Stack<>();

    }

    /**
     * Method that performs a single run through the maze by the ant.
     *
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        if (start.equals(end)) return route;
        visitedCoordinates.push(currentPosition);
        Direction dir = randomDirection();
        takeStep(dir);
        int shortCutTries = 2;
        long begin = System.currentTimeMillis();
        while (!currentPosition.equals(end)) {
//            if (System.currentTimeMillis() - 20000 > begin)
//                System.err.println("Stuck for 20 seconds");

            List<Direction> possibleDirections;
            Coordinate foundShorterRoute = findShorterRoute();
            if (foundShorterRoute != null) {
                if (positionCounter[foundShorterRoute.getX()][foundShorterRoute.getY()] < shortCutTries) {
                    positionCounter[foundShorterRoute.getX()][foundShorterRoute.getY()]++;
                    Direction tempDirection = getDirection(foundShorterRoute, currentPosition);
                    while (!foundShorterRoute.equals(currentPosition)) {
                        dir = route.removeLast();
                        revertStep(dir);
                    }
                    possibleDirections = getPossibleDirections();
                    if (possibleDirections.size() == 1)
                        takeStep(tempDirection);
                }
            }

            possibleDirections = getPossibleDirections();
            if (possibleDirections.isEmpty() || (foundShorterRoute != null && positionCounter[foundShorterRoute.getX()][foundShorterRoute.getY()] > shortCutTries)) {
                dir = route.removeLast();
                takeStepBack(dir);
            } else {
                updateDirectionProbabilities(possibleDirections, true);
                dir = weightedPossibleDirections.get();
                takeStep(dir);
            }
        }
        return route;
    }

    private Direction getDirection(Coordinate from, Coordinate to) {
        if (Math.abs(from.getX() - to.getX()) <= 1 && Math.abs(from.getY() - to.getY()) <= 1) {
            if (from.getX() < to.getX())
                return Direction.East;
            if (from.getX() > to.getX())
                return Direction.West;
            if (from.getY() < to.getY())
                return Direction.South;
            if (from.getY() > to.getY())
                return Direction.North;

        }
        return null;
    }

    private Coordinate findShorterRoute() {
        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.intToDir(i);
            Coordinate next = currentPosition.add(direction);
            if (maze.isPath(next) && !direction.isOpposite(previousDirection) && !currentPosition.equals(visitedCoordinates.peek())
                    && !next.equals(visitedCoordinates.peek()) && visitedCoordinates.contains(next) && !backtrackedCoordinates.contains(next)) {
                return next;
            }
        }
        return null;
    }

    private void revertStep(Direction dir) {
        Coordinate previousPosition = visitedCoordinates.pop();
        currentPosition = currentPosition.subtract(dir);
//        System.out.println("r: " + currentPosition + " | " + removed);
    }

    private void takeStepBack(Direction dir) {
        Coordinate removed = visitedCoordinates.pop();
        backtrackedCoordinates.push(currentPosition);
        currentPosition = currentPosition.subtract(dir);
//        System.out.println("b: " + currentPosition + " | " + removed);
    }

    private void takeStep(Direction dir) {
        previousDirection = dir;
        visitedCoordinates.push(currentPosition);
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
            if (maze.isPath(next) && !contains(visitedCoordinates, next) && !contains(backtrackedCoordinates, next)) {
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
    public void updateDirectionProbabilities(List<Direction> possibleDirections, boolean useEuclidean) {
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);

        // Reset the possible directions and add the new directions with their probabilities
        weightedPossibleDirections.reset();

        double[] pheromones = new double[possibleDirections.size()];
        double[] euclid = new double[possibleDirections.size()];

        // Get the total amount of pheromone for the possible directions (so excluding the direction where the ant came from)
        for (int i = 0; i < possibleDirections.size(); i++) {
            Direction dir = possibleDirections.get(i);
            pheromones[i] = surroundingPheromone.get(dir);
            //If there is no pheromone we still sometimes want to walk that path, so we give it a none 0 chance.
            if (pheromones[i] == 0)
                pheromones[i] = 1;
        }

        if (useEuclidean)
            euclid = getEuclidianProbabilities(possibleDirections);

        double top[] = new double[possibleDirections.size()];
        double total = 0;
        for (int i = 0; i < possibleDirections.size(); i++) {
            double alpha = 1.0;
            double beta = 0.9;
            double pheromone = Math.pow(pheromones[i], alpha);
            double heuristic = 1;
            if (useEuclidean)
                heuristic = Math.pow(euclid[i], beta);
            top[i] = pheromone * heuristic;
            total += pheromone * heuristic;
        }

        for (int i = 0; i < possibleDirections.size(); i++) {
            Direction dir = possibleDirections.get(i);
            double probability = top[i] / total;
            weightedPossibleDirections.add(dir, probability);
        }
    }

    /**
     * Get the probabilitiy for each direction based on the euclidian distance to the end point.
     *
     * @param possibleDirections the possible directions that where probabilities need to be found for
     * @return an array containing the probabilities of the possible directions
     */
    public double[] getEuclidianProbabilities(List<Direction> possibleDirections) {
        double[] factors = new double[possibleDirections.size()];

        // Calculate the combined distance of all options
        double totalDist = 0;
        for (int i = 0; i < possibleDirections.size(); i++) {
            double eucDist = currentPosition.add(possibleDirections.get(i)).getEuclidianDistance(end);
            if (eucDist == 0) {
                factors = new double[possibleDirections.size()];
                factors[i] = 1;
                return factors;
            }

            // Check if a direction brings the ant to the goal
            totalDist += eucDist;
        }

        // Calculate the factors
        double totalFactors = 0;
        for (int i = 0; i < factors.length; i++) {
            double factor = totalDist / currentPosition.add(possibleDirections.get(i)).getEuclidianDistance(end);
            factors[i] = factor;
            totalFactors += factor;
        }

        // Normalize factors
        for (int i = 0; i < factors.length; i++) {
            factors[i] = factors[i] / totalFactors;
        }

        return factors;
    }
}

