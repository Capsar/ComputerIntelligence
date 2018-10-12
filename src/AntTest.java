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

            ArrayList<Direction> directions = ant.getPossibleDirections();

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

            ArrayList<Direction> directions = ant.getPossibleDirections();

            assertEquals(1, directions.size());

            assertTrue(directions.contains(Direction.North));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
