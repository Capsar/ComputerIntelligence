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
        if (dir == null) {
            return -1;
        }

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

    /**
     * Int to Direction
     * @param number
     * @return
     */
    public static Direction intToDir(int number) {
        switch(number) {
            case -1:
                return null;
            case 0:
                return Direction.East;
            case 1:
                return Direction.North;
            case 2:
                return Direction.West;
            case 3:
                return Direction.South;
            default:
                throw new IllegalArgumentException("Number is not 0-3");
        }
    }

    /** Method that checks if two directions are opposite to each other
     *
     * @param dir
     * @return
     */
    public boolean isOpposite(Direction dir) {
        Coordinate vector1 = Direction.dirToCoordinateDelta(this);
        Coordinate vector2 = Direction.dirToCoordinateDelta(dir);

        if (vector1.getX() + vector2.getX() == 0 && vector1.getY() + vector2.getY() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public Direction getOpposite() {
        switch(this) {
            case East:
                return West;
            case North:
                return South;
            case West:
                return East;
            case South:
                return North;
            default:
                throw new IllegalArgumentException("Case statement does not match all possible values");
        }
    }
}
