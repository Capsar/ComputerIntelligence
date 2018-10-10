import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Specification of a path containing a start and end coordinate.
 */
public class PathSpecification implements Serializable{
	
    private static final long serialVersionUID = 0L;
    private Coordinate start;
    private Coordinate end;

    /**
     * Constructs a new path specification.
     * @param start the start coordinate.
     * @param end the end coordinate.
     */
    public PathSpecification(Coordinate start, Coordinate end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Get starting coordinate
     * @return starting coordinate
     */
    public Coordinate getStart() {
        return start;
    }

    /**
     * Get finish coordinate
     * @return finish coordinate
     */
    public Coordinate getEnd() {
        return end;
    }


    /**
     * Equals method for PathSpecification
     * @param other other PathSpecification
     * @return whether they're equal
     */
    public boolean equals(Object other) {
        if (!(other instanceof PathSpecification)) {
            return false;
        } else {
            PathSpecification otherSpec = (PathSpecification) other;
            return this.start.equals(otherSpec.start) && this.end.equals(otherSpec.end);
        }
    }

    /**
     * String representation of path specification
     * @return representation
     */
    public String toString() {
        return "Start: " + start + " End: " + end;
    }

    /**
     * Reads the coordinates file and returns a path specification
     * @param filePath String of the path to the file
     * @return Specification contained in the file
     */
    public static PathSpecification readCoordinates(String filePath) throws FileNotFoundException {
        Scanner scan = new Scanner(new FileReader(filePath));
        scan.useDelimiter(Pattern.compile("[,;]\\s*"));
        int startX = scan.nextInt();
        int startY = scan.nextInt();
        int endX = scan.nextInt();
        int endY = scan.nextInt();
        Coordinate start = new Coordinate(startX, startY);
        Coordinate end = new Coordinate(endX, endY);
        return new PathSpecification(start, end);
    }
}




















