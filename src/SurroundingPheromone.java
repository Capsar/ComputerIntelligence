/**
 * Class containing the pheromone information around a certain point in the maze
 */
public class SurroundingPheromone {
	
    private final double north;
    private final double south;
    private final double east;
    private final double west;
    private final double totalSurroundingPheromone;

    /**
     * Creates a surrounding pheromone object.
     * @param north the amount of pheromone in the north.
     * @param east the amount of pheromone in the east.
     * @param south the amount of pheromone in the south.
     * @param west the amount of pheromone in the west.
     */
    public SurroundingPheromone(double north, double east, double south, double west) {
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
        this.totalSurroundingPheromone = east + north + south + west;
    }

    /**
     * Get the total amount of surrouning pheromone.
     * @return total surrounding pheromone
     */
    public double getTotalSurroundingPheromone() {
        return totalSurroundingPheromone;
    }

    /**
     * Get a specific pheromone level
     * @param dir Direction of pheromone
     * @return Pheromone of dir
     */
    public double get(Direction dir) {
        switch (dir) {
            case North:
                return north;
            case East:
                return east;
            case West:
                return west;
            case South:
                return south;
        }
        throw new IllegalArgumentException("Invalid direction");
    }
}
