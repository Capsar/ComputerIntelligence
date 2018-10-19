import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Sam van Berkel on 18/10/2018.
 */
public class CoordinateTest {
    @Test
    public void get_euclidian_distance_test() {
        Coordinate co1 = new Coordinate(0, 0);
        Coordinate co2 = new Coordinate(10, 10);

        assertEquals(Math.sqrt(200), co1.getEuclidianDistance(co2));
    }

    @Test
    public void get_euclidian_distance_test_2() {
        Coordinate co1 = new Coordinate(8, 9);
        Coordinate co2 = new Coordinate(2, 3);

        assertEquals(Math.sqrt(72), co1.getEuclidianDistance(co2));
    }
}
