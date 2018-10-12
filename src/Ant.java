import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
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
    WeightedCollection weightedPossibleDirections;
    private Direction previousDirection;

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
    }

    public void setCurrentPosition(Coordinate currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setPreviousDirection(Direction previousDirection) {
        this.previousDirection = previousDirection;
    }

    /**
     * Method that performs a single run through the maze by the ant.
     * @return The route the ant found through the maze.
     */
    public Route findRoute() {
        Route route = new Route(start);

        System.out.println(currentPosition.getX() + "," + currentPosition.getY());
        Direction dir = chooseFirstDirection();
        route.add(dir);
        previousDirection = dir;

        currentPosition = currentPosition.add(dir);
        System.out.println(currentPosition.getX() + "," + currentPosition.getY());

        while(!currentPosition.equals(end)) {
            dir = chooseDirection( 0.1);

            route.add(dir);
            previousDirection = dir;

            currentPosition = currentPosition.add(dir);

            System.out.println(currentPosition.getX() + "," + currentPosition.getY());
        }

        System.out.println("route found of length: " + route.size());

        return route;
    }


    /**
     *  Randomly choose a direction for the first move.
     * @return the chosen direction
     */
    public Direction chooseFirstDirection() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Direction currentDirection = Direction.intToDir(i);

            // Check if the direction is possible to take or it does not take is back to the previous position
            if (maze.isPath(currentPosition.add(currentDirection))) {
                possibleDirections.add(currentDirection);
            }
        }

        Collections.shuffle(possibleDirections);

        return possibleDirections.get(0);
    }

    /** Choose a direction for the next move, can be random or based on the amount of feromone.
     *
     * @param randomDirChance
     * @return
     */
    public Direction chooseDirection(double randomDirChance) {
        double random = rand.nextDouble();

        if (random < randomDirChance || maze.getSurroundingPheromone(currentPosition).getTotalSurroundingPheromone() == 0) {
            updateDirectionProbabilities(true);
            Direction dir = weightedPossibleDirections.get();
            return dir;
        } else {
            updateDirectionProbabilities(false);
            return weightedPossibleDirections.get();
        }
    }


    public void updateDirectionProbabilities(boolean random) {
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);

        ArrayList<Direction> possibleDirections = getPossibleDirections();

        // Reset the possible directions and add the new directions with their probabilities
        weightedPossibleDirections.reset();
        for (Direction dir : possibleDirections) {
            double probability;
            if (random) {
                probability = 1.0 / possibleDirections.size();
            } else {
                probability = surroundingPheromone.get(dir) / surroundingPheromone.getTotalSurroundingPheromone();
            }
            weightedPossibleDirections.add(dir, probability);
        }
    }

    /**
     * Method that checks all the possible directions that are not the previous direction and don't end up in a wall our outside the maze.
     * (if there are no valid directions we take a step back to the previous position by taking the opposite direction of the last direction)
     * @return
     */
    public ArrayList<Direction> getPossibleDirections() {
        ArrayList<Direction> possibleDirections = new ArrayList<>();

        Direction opposite = null;

        for (int i = 0; i < 4; i++) {
            Direction currentDirection = Direction.intToDir(i);

            // Check if the direction is possible to take or it does not take is back to the previous position
            if (maze.isPath(currentPosition.add(currentDirection)) && !previousDirection.isOpposite(currentDirection)) {
                possibleDirections.add(currentDirection);
            } else if (previousDirection.isOpposite(currentDirection)) {
                opposite = currentDirection;
            }
        }

        // Take the opposite direction to the previous direction if no directions are available (the ant is at a dead end)
        if (possibleDirections.size() == 0) {
            possibleDirections.add(opposite);
        }

        return possibleDirections;
    }
}