package assignment3;import java.util.*;

/**
 * Class that represents the ants functionality.
 */
public class Ant extends SuperAnt {

    private int[][] visitedCoordinates;
    private int[][] backtrackedCoordinates;
    private WeightedCollection weightedPossibleDirections;
    private int[][] positionCounter;
    private Deque<Coordinate> visitedCoordinatesDeque;

    /**
     * Constructor for ant taking a Maze and PathSpecification.
     *
     * @param maze Maze the ant will be running in.
     * @param spec The path specification consisting of a start coordinate and an end coordinate.
     */
    public Ant(Maze maze, PathSpecification spec) {
        super(maze, spec);
        weightedPossibleDirections = new WeightedCollection();
        visitedCoordinates = new int[maze.getWidth()][maze.getLength()];
        backtrackedCoordinates = new int[maze.getWidth()][maze.getLength()];
        positionCounter = new int[maze.getWidth()][maze.getLength()];
        visitedCoordinatesDeque = new ArrayDeque<>();
    }

    /**
     * Method that performs a single run through the maze by the ant.
     *
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        if (start.equals(end)) return route;
        visit(currentPosition);
        Direction dir = randomDirection();
        takeStep(dir);
        int shortCutTries = 2;
        while (!currentPosition.equals(end)) {

            List<Direction> possibleDirections;
            Coordinate foundShorterRoute = findShorterRoute();
            //If a shorter route is found, go take it.
            if (foundShorterRoute != null) {
                if (positionCounter[foundShorterRoute.getX()][foundShorterRoute.getY()] < shortCutTries) {
                    positionCounter[foundShorterRoute.getX()][foundShorterRoute.getY()]++;
                    Direction tempDirection = getDirection(foundShorterRoute, currentPosition);
                    while (!foundShorterRoute.equals(currentPosition)) {
                        dir = route.removeLast();
                        revertStep(dir);
                    }
                    takeStep(tempDirection);
                }
            }

            possibleDirections = getPossibleDirections();
            //If there is no possible place to go, backtrack
            if (possibleDirections.isEmpty()) {
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
            if (maze.isPath(next) && !next.equals(visitedCoordinatesDeque.peek()) && beenHere(next)) {
                return next;
            }
        }
        return null;
    }

    private void visit(Coordinate p) {
        visitedCoordinatesDeque.push(p);
        visitedCoordinates[p.getX()][p.getY()] = 1;
    }

    private void unvisit(Coordinate p) {
        visitedCoordinatesDeque.pop();
        visitedCoordinates[p.getX()][p.getY()] = 0;
    }

    private void backTrack(Coordinate p) {
        backtrackedCoordinates[p.getX()][p.getY()] = 1;
    }

    private boolean beenHere(Coordinate p) {
        return visitedCoordinates[p.getX()][p.getY()] == 1;
    }

    private void revertStep(Direction dir) {
        unvisit(currentPosition);
        currentPosition = currentPosition.subtract(dir);
    }

    private void takeStepBack(Direction dir) {
        unvisit(currentPosition.subtract(dir));
        backTrack(currentPosition);
        currentPosition = currentPosition.subtract(dir);
    }

    public void takeStep(Direction dir) {
        visit(currentPosition);
        currentPosition = currentPosition.add(dir);
        route.add(dir);
    }

    private Direction randomDirection() {
        List<Direction> possibleDirections = getPossibleDirections();
        Collections.shuffle(possibleDirections);
        return possibleDirections.get(0);
    }

    public List<Direction> getPossibleDirections() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.intToDir(i);
            Coordinate next = currentPosition.add(direction);
            if (maze.isPath(next) && visitedCoordinates[next.getX()][next.getY()] == 0 && backtrackedCoordinates[next.getX()][next.getY()] == 0) {
                possibleDirections.add(direction);
            }
        }

        return possibleDirections;
    }

    /**
     * Update the list of weighted probabilities by adding all the possible directions with their relative probabilities.
     *
     * @param possibleDirections the directions that are possible to take
     */
    public void updateDirectionProbabilities(List<Direction> possibleDirections, boolean useEuclidean) {
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition, possibleDirections);

        // Reset the possible directions and add the new directions with their probabilities
        weightedPossibleDirections.reset();

        double[] pheromones = new double[possibleDirections.size()];
        double total = 0;

        // Get the total amount of pheromone for the possible directions (so excluding the direction where the ant came from)
        for (int i = 0; i < possibleDirections.size(); i++) {
            Direction dir = possibleDirections.get(i);
            pheromones[i] = surroundingPheromone.get(dir);
            //If there is no pheromone we still sometimes want to walk that path, so we give it a none 0 chance.
            total += pheromones[i];
        }

        if (useEuclidean) {
            double[] euclid = getEuclideanProbabilities(possibleDirections);
            double alpha = 1.0;
            double beta = 0.5;
            total = 0;
            for (int i = 0; i < possibleDirections.size(); i++) {
                double pheromone = Math.pow(pheromones[i], alpha);
                double heuristic = Math.pow(euclid[i], beta);

                pheromones[i] = pheromone * heuristic;
                total += pheromone * heuristic;
            }
        }


        for (int i = 0; i < possibleDirections.size(); i++) {
            Direction dir = possibleDirections.get(i);
            double probability = pheromones[i] / total;
            weightedPossibleDirections.add(dir, probability);
        }
    }

    /**
     * Get the probabilitiy for each direction based on the euclidian distance to the end point.
     *
     * @param possibleDirections the possible directions that where probabilities need to be found for
     * @return an array containing the probabilities of the possible directions
     */
    public double[] getEuclideanProbabilities(List<Direction> possibleDirections) {
        double[] factors = new double[possibleDirections.size()];

        // Calculate the combined distance of all options
        double totalDist = 0;
        double[] eucDist = new double[possibleDirections.size()];
        for (int i = 0; i < possibleDirections.size(); i++) {
            eucDist[i] = currentPosition.add(possibleDirections.get(i)).getEuclidianDistance(end);
            if (eucDist[i] == 0) {
                factors = new double[possibleDirections.size()];
                factors[i] = 1;
                return factors;
            }

            // Check if a direction brings the ant to the goal
            totalDist += eucDist[i];
        }

        // Calculate the factors
        double totalFactors = 0;
        for (int i = 0; i < factors.length; i++) {
            double factor = totalDist / eucDist[i];
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

