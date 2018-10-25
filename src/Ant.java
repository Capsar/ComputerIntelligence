import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 * Class that represents the ants functionality.
 */
public class Ant {
	
    private Maze maze;
    private Coordinate start;
    private Coordinate end;
    private Coordinate currentPosition;
    private static Random rand;
    private WeightedCollection weightedPossibleDirections;
    private Direction previousDirection;
    private ArrayList<Coordinate> visitedCoordinates;
    private Route route;
    private ArrayList<Coordinate> backtrackedCoordinates;

    /**
     * Constructor for ant taking a Maze and PathSpecification.
     * @param maze Maze the ant will be running in.
     * @param spec The path specification consisting of a start coordinate and an end coordinate.
     */
    public Ant(Maze maze, PathSpecification spec) {
        this.maze = maze;
        this.start = spec.getStart();
        this.end = spec.getEnd();
        this.currentPosition = start;
        if (rand == null) {
            rand = new Random();
        }
        weightedPossibleDirections = new WeightedCollection();
        visitedCoordinates = new ArrayList<>();
        route = new Route(start);
        backtrackedCoordinates = new ArrayList<>();
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setPreviousDirection(Direction previousDirection) {
        this.previousDirection = previousDirection;
    }

    public Route getRoute() {
        return route;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public Maze getMaze() {
        return maze;
    }

    public Coordinate getStart() {
        return start;
    }

    public Coordinate getEnd() {
        return end;
    }

    /**
     * Method that performs a single run through the maze by the ant.
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        if (start.equals(end)) {
            return route;
        }
        addVisitedCoordinate(start);

        Direction dir = chooseFirstDirectionRandom();
        currentPosition = currentPosition.add(dir);
        previousDirection = dir;

        route.add(dir);
        addVisitedCoordinate(currentPosition);

        ArrayList<Direction> possibleDirections = getPossibleDirections(true);

        long begin = System.currentTimeMillis();
        while(!currentPosition.equals(end)) {
            if(System.currentTimeMillis() - begin > 10000) {
                System.out.println(currentPosition);
            }

            if (possibleDirections.size() == 0) {
                deadEndHandler();
                possibleDirections = getPossibleDirections(false);
            }

            updateDirectionProbabilities(possibleDirections, true);
            dir = weightedPossibleDirections.get();

            takeStep(dir);

            // Check if there is a loop
            if (visitedCoordinates.contains(currentPosition)) {
                loopHandler();
            }

            addVisitedCoordinate(currentPosition);
            possibleDirections = getPossibleDirections(true);
        }

        //System.out.println("found a route of length: " + route.size());

        return route;
    }

    /**
     * Adds a coordinate to the visited list if its a crossroad.
     * @param coordinate the coordinate to be added
     */
    public void addVisitedCoordinate(Coordinate coordinate) {
        if (getPossibleDirections(true).size() > 1) {
            visitedCoordinates.add(coordinate);
            if (coordinate.equals(new Coordinate(75, 73))) {
                System.out.println("added test");
            }
        }
    }

    public void takeStep(Direction dir) {
        currentPosition = currentPosition.add(dir);

        route.add(dir);

        previousDirection = dir;
    }

    /**
     *  Randomly choose a direction for the first move.
     * @return the chosen direction
     */
    public Direction chooseFirstDirectionRandom() {
        ArrayList<Direction> possibleDirections = getPossibleDirections(false);

        Collections.shuffle(possibleDirections);

        return possibleDirections.get(0);
    }

    /**
     * Update the list of weighted probabilities by adding all the possible directions with their relative probabilities.
     * @param possibleDirections the directions that are possible to take
     * @param usePheromones boolean that determines if the possibilities need to be based on the amount of pheromones
     */
    public void updateDirectionProbabilities(ArrayList<Direction> possibleDirections, boolean usePheromones) {
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);

        // Reset the possible directions and add the new directions with their probabilities
        weightedPossibleDirections.reset();

        // Get the total amount of pheromone for the possible directions (so excluding the direction where the ant came from)
        double totalPheromone = 0;
        for (Direction dir : possibleDirections) {
            double pheromone = surroundingPheromone.get(dir);
            totalPheromone += pheromone;

            if (pheromone == 0) {
                usePheromones = false;
            }
        }

        if (!usePheromones) {
            double[] probabilities = getEuclidianProbabilities(possibleDirections);
            for (int i = 0; i < possibleDirections.size(); i++) {
                weightedPossibleDirections.add(possibleDirections.get(i), probabilities[i]);
            }
        } else {
            for (Direction dir : possibleDirections) {
                double probability;
                probability = surroundingPheromone.get(dir) / totalPheromone;
                weightedPossibleDirections.add(dir, probability);
            }
        }
    }

    /**
     * Get the probabilitiy for each direction based on the euclidian distance to the end point.
     * @param possibleDirections the possible directions that where probabilities need to be found for
     * @return an array containing the probabilities of the possible directions
     */
    public double[] getEuclidianProbabilities(ArrayList<Direction> possibleDirections) {
        double[] factors = new double[possibleDirections.size()];

        // Calculate the combined distance of all options
        double totalDist = 0;
        for (int i = 0; i < possibleDirections.size(); i++) {
            double eucDist = currentPosition.add(possibleDirections.get(i)).getEuclidianDistance(end);

            // Check if a direction brings the ant to the goal
            if (eucDist == 0) {
                factors = new double[factors.length];
                for (int j = 0; j < factors.length; j++) {
                    if (j == i) {
                        factors[j] = 1;
                    } else {
                        factors[j] = 0;
                    }
                }
                return factors;
            } else {
                totalDist += eucDist;
            }
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
    /**
     * Method that checks all the possible directions that are not the previous direction and don't end up in a wall our outside the maze.
     * (if there are no valid directions we take a step back to the previous position by taking the opposite direction of the last direction)
     * @return
     */
    public ArrayList<Direction> getPossibleDirections(boolean previousDirectionPrevention) {
        ArrayList<Direction> possibleDirections = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Direction currentDirection = Direction.intToDir(i);

            // Check if the direction is to take results in a path
            if (maze.isPath(currentPosition.add(currentDirection))) {
                // Check if we're allowed to take the direction where we came from
                if (!previousDirectionPrevention || previousDirection == null || !previousDirection.isOpposite(currentDirection)) {
                    possibleDirections.add(currentDirection);
                }
            }
        }

        return possibleDirections;
    }

    /**
     * Handler for when a loop is encountered, steps are taken back along the route and removed.
     */
    public void loopHandler() {
        Coordinate startOfLoop = currentPosition;

        // Remove the last step from the route and take the step back until the ant is at the start of the loop
        do  {
            visitedCoordinates.remove(currentPosition);
            Direction lastDirection = route.removeLast();
            currentPosition = currentPosition.subtract(lastDirection);
        } while (!currentPosition.equals(startOfLoop));
    }

    public void adjacentHandler() {

    }

    /**
     * Handler for when a dead end is encountered.
     */
    public void deadEndHandler() {
        ArrayList<Direction> possibleDirections = getPossibleDirections(true);

        while(possibleDirections.size() < 2) {
            visitedCoordinates.remove(currentPosition);
            previousDirection = route.removeLast();
            currentPosition = currentPosition.subtract(previousDirection);

            possibleDirections = getPossibleDirections(true);
        }

        //maze.setWall(currentPosition.add(previousDirection));
    }
}