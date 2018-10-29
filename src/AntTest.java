import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

            ant.setCurrentPosition(new Coordinate(16, 2));
            ant.takeStep(Direction.South);
            List<Direction> directions = ant.getPossibleDirections();

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

            ant.setCurrentPosition(new Coordinate(24, 13));
            ant.takeStep(Direction.South);
            List<Direction> directions = ant.getPossibleDirections();
            assertEquals(0, directions.size());
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

            assertEquals(2, ant.getPossibleDirections().size());
            double[] factors = ant.getEuclideanProbabilities(ant.getPossibleDirections());

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
    public void get_euclidean_factors2() {
        try {
            Maze maze = Maze.createMaze("./data/medium maze.txt");

            PathSpecification spec = PathSpecification.readCoordinates("./data/medium coordinates.txt");

            Ant ant = new Ant(maze, spec);

            ant.setCurrentPosition(new Coordinate(41, 20));
            ant.takeStep(Direction.South);

            assertEquals(3, ant.getPossibleDirections().size());
            double[] factors = ant.getEuclideanProbabilities(ant.getPossibleDirections());

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
