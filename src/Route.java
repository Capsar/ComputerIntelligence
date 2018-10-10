import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class representing a route.
 */
public class Route implements Serializable {
	
    private static final long serialVersionUID = 0L;
    private ArrayList<Direction> route;
    private Coordinate start;

    /**
     * Route takes a starting coordinate to initialize
     * @param start starting coordinate
     */
    public Route(Coordinate start) {
        route = new ArrayList<>();
        this.start = start;
    }

    /**
     * After taking a step we add the direction we moved in
     * @param dir Direction we moved in
     */
    public void add(Direction dir) {
        route.add(dir);
    }


    /**
     * Returns the length of the route
     * @return length of the route
     */
    public int size() {
        return route.size();
    }

    /**
     * Getter for the list of directions
     * @return list of directions
     */
    public ArrayList<Direction> getRoute() {
        return route;
    }

    /**
     * Getter for the starting coordinate
     * @return the starting coordinate
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * Function that checks whether a route is smaller than another route
     * @param other the other route
     * @return whether the route is shorter
     */
    public boolean shorterThan(Route other) {
        return this.size() < other.size();
    }

    /**
     * Take a step back in the route and return the last direction
     * @return last direction
     */
    public Direction removeLast() {
        return route.remove(route.size() - 1);
    }

    /**
     * Build a string representing the route as the format specified in the manual.
     * @return string with the specified format of a route
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Direction dir : route) {
            sb.append(Direction.dirToInt(dir)).append(";\n");
        }
        return sb.toString();
    }
    

    /**
     * Equals method for route
     * @param other Other route
     * @return boolean whether they are equal
     */
    public boolean equals(Object other) {
        if (!(other instanceof Route)) {
            return false;
        } else {
            Route otherR = (Route) other;
            return this.start.equals(otherR.start)
                    && this.route.equals(otherR.route);
        }
    }

    /**
     * Method that implements the specified format for writing a route to a file.
     * @param filePath path to route file.
     * @throws FileNotFoundException
     */
    public void writeToFile(String filePath) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(filePath);
        String sb = String.valueOf(route.size()) + ";\n" +
                start + ";\n" +
                this.toString();
        pw.write(sb);
        pw.close();
    }
}
