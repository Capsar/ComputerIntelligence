import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Sam van Berkel on 11/10/2018.
 */
public class AntTest {

    @Test
    public void get_possible_directions_cross_road() {
        try {
            Maze maze = Maze.createMaze("./data/easy maze.txt");
            PathSpecification spec = PathSpecification.readCoordinates("./data/easy coordinates.txt");

            Ant ant = new Ant(maze, spec);

            ant.setCurrentPosition(new Coordinate(16, 3));
            ant.setPreviousDirection(Direction.South);

            ArrayList<Direction> directions = ant.getPossibleDirections(true);

            assertEquals(3, directions.size());

            assertFalse(directions.contains(Direction.North));
            assertTrue(directions.contains(Direction.East));
            assertTrue(directions.contains(Direction.South));
            assertTrue(directions.contains(Direction.West));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void get_possible_directions_dead_end() {
        try {
            Maze maze = Maze.createMaze("./data/easy maze.txt");
            PathSpecification spec = PathSpecification.readCoordinates("./data/easy coordinates.txt");

            Ant ant = new Ant(maze, spec);

            ant.setCurrentPosition(new Coordinate(24, 14));
            ant.setPreviousDirection(Direction.South);

            ArrayList<Direction> directions = ant.getPossibleDirections(true);

            assertEquals(0, directions.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void dead_end_handler() {
        try {
            Maze maze = Maze.createMaze("./testMazes/dead end maze.txt");
            PathSpecification spec = PathSpecification.readCoordinates("./testMazes/dead end coordinates.txt");

            Ant ant = new Ant(maze, spec);

            ant.setCurrentPosition(new Coordinate(0, 0));
            assertEquals(true, ant.getMaze().isPath(new Coordinate(1, 2)));

            ant.takeStep(Direction.South);
            ant.takeStep(Direction.South);
            ant.takeStep(Direction.East);
            ant.takeStep(Direction.East);
            ant.takeStep(Direction.East);

            System.out.println("cp: " + ant.getCurrentPosition());

            assertEquals(5, ant.getRoute().size());
            ant.deadEndHandler(Direction.West);
            assertEquals(0, ant.getCurrentPosition().getX());
            assertEquals(2, ant.getCurrentPosition().getY());
            assertEquals(2, ant.getRoute().size());
            ant.takeStep(Direction.South);
            ant.takeStep(Direction.South);
            assertEquals(4, ant.getRoute().size());
            assertEquals(false, ant.getMaze().isPath(new Coordinate(1, 2)));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void get_euclidian_factors() {
        try {
            Maze maze = Maze.createMaze("./data/easy maze.txt");

            PathSpecification spec = PathSpecification.readCoordinates("./data/easy coordinates.txt");

            Ant ant = new Ant(maze, spec);

            ant.setCurrentPosition(new Coordinate(0, 0));

            assertEquals(2, ant.getPossibleDirections(false).size());
            double[] factors = ant.getEuclidianFactors(ant.getPossibleDirections(false));

            double eucDist1 = Math.sqrt(725);
            double eucDist2 = Math.sqrt(745);
            double totalDist = eucDist1 + eucDist2;
            double totalFactors = totalDist / eucDist1 + totalDist / eucDist2;

            assertEquals(2, factors.length);

            assertEquals(totalDist / eucDist1 / totalFactors, factors[0]);
            assertEquals(totalDist / eucDist2 / totalFactors, factors[1]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void get_euclidian_factors2() {
        try {
            Maze maze = Maze.createMaze("./data/medium maze.txt");

            PathSpecification spec = PathSpecification.readCoordinates("./data/medium coordinates.txt");

            Ant ant = new Ant(maze, spec);

            ant.setCurrentPosition(new Coordinate(41, 20));
            ant.takeStep(Direction.South);

            assertEquals(3, ant.getPossibleDirections(true).size());
            double[] factors = ant.getEuclidianFactors(ant.getPossibleDirections(true));

            double eucDist1 = Math.sqrt(5);
            double eucDist2 = Math.sqrt(5);
            double eucDist3 = 1;
            double totalDist = eucDist1 + eucDist2 + eucDist3;
            double totalFactors = totalDist / eucDist1 + totalDist / eucDist2 + totalDist / eucDist3;

            assertEquals(3, factors.length);

            assertEquals(0, factors[0]);
            assertEquals(0, factors[1]);
            assertEquals(1, factors[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
