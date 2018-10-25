import java.util.*;

/**
 * Class that represents the ants functionality.
 */
public class Ant2 {

    private static Random rand;
    private Maze maze;
    private Coordinate start;
    private Coordinate end;
    private Coordinate currentPosition;
    private Route route;
    private Stack<Coordinate> visitedCoordinates;
    private Stack<Coordinate> backtrackedCoordinates;
    private WeightedCollection weightedPossibleDirections;
    private Coordinate previousPosition;
    private boolean lastStepWasForward;

    /**
     * Constructor for ant taking a Maze and PathSpecification.
     *
     * @param maze Maze the ant will be running in.
     * @param spec The path specification consisting of a start coordinate and an end coordinate.
     */
    public Ant2(Maze maze, PathSpecification spec) {
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
        previousPosition = start;
        lastStepWasForward = true;
        long begin = System.currentTimeMillis();
        while (!currentPosition.equals(end)) {
            if(System.currentTimeMillis() - 20000 > begin)
                System.err.println("Stuck for 20 seconds");

            List<Direction> possibleDirections = getPossibleDirections();
            if (possibleDirections.isEmpty()) {
                dir = route.removeLast().getOpposite();
                takeStepBack(dir);
            } else {
//                Coordinate foundShorterRoute = findShorterRoute();
//                if (foundShorterRoute != null && lastStepWasForward) {
//                    System.out.println("Found a faster route: from " + foundShorterRoute + " to " + currentPosition);
//                    Direction tempDirection = getDirection(foundShorterRoute, currentPosition);
//                    while (!foundShorterRoute.equals(currentPosition)) {
//                        dir = route.removeLast().getOpposite();
//                        revertStep(dir);
//                    }
//                    visitedCoordinates.push(currentPosition);
//                    takeStep(tempDirection);
//                    System.out.println("Shortcut completed.");
//                } else {
                    updateDirectionProbabilities(possibleDirections);
                    dir = weightedPossibleDirections.get();
                    takeStep(dir);
//                }
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
            if (!next.equals(previousPosition) && visitedCoordinates.contains(next) && !backtrackedCoordinates.contains(next)) {
                return next;
            }
        }
        return null;
    }

    private void revertStep(Direction dir) {
        Coordinate removed = visitedCoordinates.pop();
        previousPosition = currentPosition;
        currentPosition = currentPosition.add(dir);
        lastStepWasForward = false;
//        System.out.println("r: " + currentPosition + " | " + removed);
    }

    private void takeStepBack(Direction dir) {
        Coordinate removed = visitedCoordinates.pop();
        backtrackedCoordinates.push(removed);

        previousPosition = currentPosition;
        currentPosition = currentPosition.add(dir);
        lastStepWasForward = false;
//        System.out.println("b: " + currentPosition + " | " + removed);
    }

    private void takeStep(Direction dir) {
        lastStepWasForward = true;
        previousPosition = currentPosition;
        currentPosition = currentPosition.add(dir);
        route.add(dir);
        visitedCoordinates.push(currentPosition);
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
        for(Coordinate coordinate : stack) {
            if(coordinate.equals(pos))
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
        SurroundingPheromone surroundingPheromone = maze.getSurroundingPheromone(currentPosition);

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

