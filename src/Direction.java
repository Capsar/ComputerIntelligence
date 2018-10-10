import java.util.EnumMap;

/**
 * Enum representing the directions an ant can take.
 */
public enum Direction {
	
    North,
    East,
    West,
    South;

	//all directions in a vector
    private static Coordinate northVector = new Coordinate(0,-1);
    private static Coordinate southVector = new Coordinate(0,1);
    private static Coordinate westVector = new Coordinate(-1,0);
    private static Coordinate eastVector = new Coordinate(1,0);
    private static EnumMap<Direction, Coordinate> dirToCoordinateDeltaMap = buildDirToCoordinateDelta();

    /**
     * Creates a map with a direction linked to its (direction) vector.
     * @return an enummap.
     */
    private static EnumMap<Direction, Coordinate> buildDirToCoordinateDelta() {
        EnumMap<Direction, Coordinate> map = new EnumMap<>(Direction.class);
        map.put(Direction.East, eastVector);
        map.put(Direction.West, westVector);
        map.put(Direction.North, northVector);
        map.put(Direction.South, southVector);
        return map;
    }

    /**
     * Get vector (coordinate) of a certain direction.
     * @param dir the direction
     * @return the coordinate
     */
    public static Coordinate dirToCoordinateDelta(Direction dir) {
        return dirToCoordinateDeltaMap.get(dir);
    }

    /**
     * Direction to an int.
     * @param dir the direction.
     * @return an integer from 0-3.
     */
    public static int dirToInt(Direction dir) {
        switch(dir) {
        	case East:
        		return 0;
            case North:
                return 1;
            case West:
                return 2;
            case South:
                return 3;
            default:
                throw new IllegalArgumentException("Case statement does not match all possible values");
        }
    }
}
