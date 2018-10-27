import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Scanner;

/**
 * Class that holds all the maze data. This means the pheromones, the open and blocked tiles in the system as
 * well as the starting and end coordinates.
 */
public class Maze {
    private int width;
    private int length;
    private int[][] walls;
    private double[][] globalPheromones;
    private double[][] localPheromones;

    /**
     * Constructor of a maze
     * @param walls int array of tiles accessible (1) and non-accessible (0)
     * @param width width of Maze (horizontal)
     * @param length length of Maze (vertical)
     */
    public Maze(int[][] walls, int width, int length) {
        this.walls = walls;
        this.length = length;
        this.width = width;
        initializePheromones();
    }

    /**
     * Initialize pheromones to a start value.
     */
    private void initializePheromones() {
        localPheromones = new double[width][length];
        globalPheromones = new double[width][length];
    }

    /**
     * Reset the maze for a new shortest path problem.
     */
    public void reset() {
        initializePheromones();
    }

    public void resetLocalPheromones() {
        localPheromones = new double[width][length];
    }

    /**
     * Update the pheromones along a certain route according to a certain Q
     * @param route The route of the ants
     * @param Q Normalization factor for amount of dropped pheromone
     */
    public synchronized void addLocalPheromoneRoute(Route route, double Q) {
        double pheromonePerCoordinate = Q / (route.size() + 1);
        Coordinate currentLoc = route.getStart();
        localPheromones[currentLoc.getX()][currentLoc.getY()] += pheromonePerCoordinate;

        for (Direction dir: route.getRoute()) {
            currentLoc = currentLoc.add(dir);
            localPheromones[currentLoc.getX()][currentLoc.getY()] += pheromonePerCoordinate;
        }
    }

    /**
     * Update the pheromones along a certain route according to a certain Q
     * @param route The route of the ants
     * @param Q Normalization factor for amount of dropped pheromone
     */
    public void addGlobalPheromoneRoute(Route route, double Q) {
        double pheromonePerCoordinate = Q / (route.size() + 1);
        Coordinate currentLoc = route.getStart();
        globalPheromones[currentLoc.getX()][currentLoc.getY()] += pheromonePerCoordinate;

        for (Direction dir: route.getRoute()) {
            currentLoc = currentLoc.add(dir);
            globalPheromones[currentLoc.getX()][currentLoc.getY()] += pheromonePerCoordinate;
        }
    }

    /**
     * Update pheromones for a list of routes
     * @param routes A list of routes
     * @param Q Normalization factor for amount of dropped pheromone
     */
    public void addLocalPheromoneRoutes(List<Route> routes, double Q) {
        for (Route r : routes) {
            addLocalPheromoneRoute(r, Q);
        }
    }

    /**
     * Evaporate pheromone
     * @param rho evaporation factor
     */
    public void evaporate(double rho) {
        for (int y = 0; y < globalPheromones[0].length; y++) {
            for (int x = 0; x < globalPheromones.length; x++) {
                globalPheromones[x][y] = globalPheromones[x][y] * (1 - rho);
            }
        }
        resetLocalPheromones();
    }

    /**
     * Width getter
     * @return width of the maze
     */
    public int getWidth() {
        return width;
    }

    /**
     * Length getter
     * @return length of the maze
     */
    public int getLength() {
        return length;
    }


    /**
     * Returns a the amount of pheromones on the neighbouring positions (N/S/E/W).
     * @param position The position to check the neighbours of.
     * @return the pheromones of the neighbouring positions.
     */
    public SurroundingPheromone getSurroundingPheromone(Coordinate position) {
        double[] pheromones = new double[4];

        for (int i = 0; i < 4; i++) {
            Coordinate co = position.add(Direction.intToDir(i));
            pheromones[i] = getPheromone(co);
        }

        return new SurroundingPheromone(pheromones[1], pheromones[3], pheromones[2], pheromones[0]);
    }

    /**
     * Pheromone getter for a specific position. If the position is not in bounds returns 0
     * @param pos Position coordinate
     * @return pheromone at point
     */
    private double getPheromone(Coordinate pos) {
        if (inBounds(pos)) {
            return globalPheromones[pos.getX()][pos.getY()] + localPheromones[pos.getX()][pos.getY()];
        } else {
            return 0;
        }
    }


    /**
     * Check whether a coordinate lies in the current maze.
     * @param position The position to be checked
     * @return Whether the position is in the current maze
     */
    public boolean inBounds(Coordinate position) {
        return position.xBetween(0, width) && position.yBetween(0, length);
    }

    /**
     * Representation of Maze as defined by the input file format.
     * @return String representation
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(width);
        sb.append(' ');
        sb.append(length);
        sb.append(" \n");
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++ ) {
                sb.append(walls[x][y]);
                sb.append(' ');
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Method that builds a mze from a file.
     * @param filePath Path to the file
     * @return A maze object with pheromones initialized to 0's inaccessible and 1's accessible.
     */
    public static Maze createMaze(String filePath) throws FileNotFoundException {
        Scanner scan = new Scanner(new FileReader(filePath));
        int width = scan.nextInt();
        int length = scan.nextInt();
        int[][] mazeLayout = new int[width][length];
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                mazeLayout[x][y] = scan.nextInt();
            }
        }
        scan.close();
        return new Maze(mazeLayout, width, length);
    }

    /**
     * Method that checks if a coordinate is a path.
     * @param coordinate coordinate to be checked
     * @return boolean
     */
    public boolean isPath(Coordinate coordinate) {
        if (inBounds(coordinate) && walls[coordinate.getX()][coordinate.getY()] == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates a text file with the amount of pheromone in each cell.
     */
    public void createPheromoneFile() {
        File f = new File("data/pheromones.txt");

        try {
            f.createNewFile();

            PrintWriter pw = new PrintWriter (f);
            pw.println (width + " " + length);

            for (int y = 0; y < length; y++) {
                String currentLine = "";
                for (int x = 0; x < width; x++) {
                    DecimalFormat df = new DecimalFormat("#.##");
                    double value = globalPheromones[x][y];
                    if (walls[x][y] == 0) {
                        value = -1;
                    }

                    currentLine += df.format(value) + " ";
                }
                pw.println(currentLine);
            }

            pw.close ();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
