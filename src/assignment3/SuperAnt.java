package assignment3; /**
 * Super class for Ant containing al basic methods.
 */
public class SuperAnt {

    protected Maze maze;
    protected Coordinate start;
    protected Coordinate end;
    protected Coordinate currentPosition;
    protected Route route;

    public SuperAnt(Maze maze, PathSpecification spec) {
        this.maze = maze;
        this.start = spec.getStart();
        this.end = spec.getEnd();
        this.currentPosition = start;
        route = new Route(this.currentPosition);

    }

    public Maze getMaze() {
        return maze;
    }

    public Route getRoute() {
        return route;
    }

    public Coordinate getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(Coordinate pos) {
        currentPosition = pos;
    }
}
