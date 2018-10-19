import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
    private HashMap<Coordinate, Boolean> exploredCoordinates;

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
        exploredCoordinates = new HashMap<>();
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

    /**
     * Method that performs a single run through the maze by the ant.
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        addVisitedCoordinate(start);

        Direction dir = chooseFirstDirectionRandom();
        currentPosition = currentPosition.add(dir);

        route.add(dir);
        addVisitedCoordinate(currentPosition);

        previousDirection = dir;

        ArrayList<Direction> possibleDirections = getPossibleDirections(true);

        while(!currentPosition.equals(end)) {
            if (possibleDirections.size() == 0) {
                deadEndHandler(previousDirection.getOpposite());
                possibleDirections = getPossibleDirections(true);
            }

            updateDirectionProbabilities(possibleDirections, false);
            dir = weightedPossibleDirections.get();

            takeStep(dir);

            // Check if there is a loop
            if (visitedCoordinates.contains(currentPosition)) {
                loopHandler();
                possibleDirections = new ArrayList<>();
                possibleDirections.add(chooseFirstDirectionRandom());
            } else {
                addVisitedCoordinate(currentPosition);
                possibleDirections = getPossibleDirections(true);
            }
            //System.out.println("cp: " + currentPosition);
        }

        System.out.println("found a route of length: " + route.size());

        return route;
    }

    public void addVisitedCoordinate(Coordinate coordinate) {
        if (getPossibleDirections(true).size() > 1) {
            visitedCoordinates.add(coordinate);
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
     *
     */
    public void updateDirectionProbabilities(ArrayList<Direction> possibleDirections, boolean random) {
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);

        // Reset the possible directions and add the new directions with their probabilities
        weightedPossibleDirections.reset();

        // Get the total amount of pheromone for the possible directions (so excluding the direction where the ant came from)
        double totalPheromone = 0;
        for (Direction dir : possibleDirections) {
            double pheromone = surroundingPheromone.get(dir);
            totalPheromone += pheromone;

            if (pheromone == 0) {
                random = true;
            }
        }

        // Take a random direction if there is no pheromone on any of the directions
        if (totalPheromone == 0) {
            random = true;
        }

        for (Direction dir : possibleDirections) {
            double probability;
            if (random) {

                probability = 1.0 / possibleDirections.size();
            } else {
                probability = surroundingPheromone.get(dir) / totalPheromone;
            }
            weightedPossibleDirections.add(dir, probability);
        }
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

    public void loopHandler() {
        Coordinate startOfLoop = currentPosition;

        visitedCoordinates.remove(currentPosition);

        Direction lastDirection = route.removeLast();
        currentPosition = currentPosition.subtract(lastDirection);

        while (!currentPosition.equals(startOfLoop)) {
            visitedCoordinates.remove(currentPosition);
            lastDirection = route.removeLast();
            currentPosition = currentPosition.subtract(lastDirection);
        }

        addVisitedCoordinate(currentPosition);

        // Check if the ant is back at the start
        if (route.size() == 0) {
            chooseFirstDirectionRandom();
        } else {
            // Set the previous direction to the last taken direction before the loop
            previousDirection = route.getRoute().get(route.getRoute().size() - 1).getOpposite();
        }
        //System.out.println("returned from loop");
    }

    public void deadEndHandler(Direction directionToTake) {
//        System.out.println("dead end at: " + currentPosition);
        visitedCoordinates.remove(currentPosition);
        currentPosition = currentPosition.add(directionToTake);
        previousDirection = directionToTake;
        //System.out.println(route.size());
        route.removeLast();

        ArrayList<Direction> possibleDirections = getPossibleDirections(true);

        while(possibleDirections.size() < 2) {
            visitedCoordinates.remove(currentPosition);
            currentPosition = currentPosition.add(possibleDirections.get(0));
            previousDirection = possibleDirections.get(0);
            route.removeLast();

            possibleDirections = getPossibleDirections(true);
        }

        maze.setWall(currentPosition.subtract(previousDirection));
    }
}