package assignment2;

import assignment2.rl.Agent;
import assignment2.rl.State;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import assignment2.rl.mysolution.StaticRunMe;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Sam van Berkel on 27/09/2018.
 */
public class mazeController implements Initializable {

    @FXML
    private GridPane mazeGridPane;
    private static final int MAX_TRAILS = 100;
    private double epsilon = 0.1;
    private double alfa = 0.7;
    private double gamma = 0.9;

    /**
     * Returns the node at a specific row and column of a gridpane.
     *
     * @param row      the row where the node is
     * @param column   the column where the node is
     * @param gridPane the gridpane that contains the node
     * @return the node at the given location
     */
    public static Node getNodeByRowColumnIndex(final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if (gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toyMaze();
    }


    public void easyMaze() {
        StaticRunMe.initEasyMaze();
        createMazeGrid();
    }

    public void toyMaze() {
        StaticRunMe.initToyMaze();
        createMazeGrid();
    }

    /**
     * Creates the grid for the maze in the gui, loads the walls with black and the paths with white.
     */
    public void createMazeGrid() {
        State[][] states = StaticRunMe.maze.getStates();
        int mazeWidth = states[0].length;
        int mazeHeight = states.length;
        mazeGridPane.getColumnConstraints().clear();
        mazeGridPane.getRowConstraints().clear();

        for (int i = 0; i < mazeWidth; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / mazeWidth);
            mazeGridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < mazeHeight; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / mazeHeight);
            mazeGridPane.getRowConstraints().add(rowConst);
        }

        for (int row = 0; row < mazeHeight; row++) {
            for (int col = 0; col < mazeWidth; col++) {
                AnchorPane pane = new AnchorPane();
                if (states[row][col].getType().equals("0")) {
                    pane.setStyle("-fx-background-color: black");
                } else {
                    pane.setStyle("-fx-background-color: white");
                }
                mazeGridPane.add(pane, col, row);
            }
        }
    }

    /**
     * Starts the agent in the maze on a new thread.
     *
     * @throws InterruptedException
     */
    @FXML
    public void launchMaze() throws InterruptedException {
        Thread thread = new Thread() {
            public void run() {
                ArrayList<Integer> x = new ArrayList<Integer>();
                ArrayList<Integer> y = new ArrayList<Integer>();

                //keep learning until you decide to stop
                int numberOfTrails = 0;
                Agent robot = StaticRunMe.robot;
                robot.reset();

                while (true) {
                    // Get the current node and set its background back to white
                    Node currentNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
                    currentNode.setStyle("-fx-background-color: white");

                    numberOfTrails = StaticRunMe.loop(x, y, epsilon, alfa, gamma, numberOfTrails);
                    //TODO figure out a stopping criterion

                    // Get the current node and set its background back to red to mark the location of the agent
                    Node nextNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
                    nextNode.setStyle("-fx-background-color: red");
                    if(numberOfTrails == MAX_TRAILS)
                        break;

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("\nFinished run with " + numberOfTrails + " steps.");
            }
        };

        thread.start();


    }

    /**
     * Run the agent without gui, which is faster.
     */
    public void noGUI() {
        //Variable to change and plot

        for(double v = 0.0; v <= 1.0; v+=0.1) {
            v = StaticRunMe.round(v, 1);
            alfa = v;
            for (int i = 0; i < 10; i++) {
                StaticRunMe.init();
                ArrayList<Integer> x = new ArrayList<Integer>();
                ArrayList<Integer> y = new ArrayList<Integer>();

                //keep learning until you decide to stop
                int numberOfTrails = 0;
                Agent robot = StaticRunMe.robot;
                robot.reset();

                while (true) {
                    numberOfTrails = StaticRunMe.loop(x, y, epsilon, alfa, gamma, numberOfTrails);
                    if (numberOfTrails == MAX_TRAILS)
                        break;
                }
                System.out.println("");
            }
            System.out.println("\n\n\n");
        }
        System.out.println("Finished run");
    }

}
