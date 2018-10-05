import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import tudelft.rl.*;
import tudelft.rl.mysolution.StaticRunMe;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Sam van Berkel on 27/09/2018.
 */
public class mazeController implements Initializable {

    @FXML
    private GridPane mazeGridPane;

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
        StaticRunMe.init();
        createMazeGrid();
    }

    /**
     * Initializes the global variables of the maze and agent.
     */

    /**
     * Creates the grid for the maze in the gui, loads the walls with black and the paths with white.
     */
    public void createMazeGrid() {
        State[][] states = StaticRunMe.maze.getStates();
        int mazeWidth = states[0].length;
        int mazeHeight = states.length;

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
                boolean stop = false;

                ArrayList<Integer> x = new ArrayList<Integer>();
                ArrayList<Integer> y = new ArrayList<Integer>();

                //keep learning until you decide to stop
                int numberOfActions = 0;
                Agent robot = StaticRunMe.robot;

                while (!stop) {
                    // Get the current node and set its background back to white
                    Node currentNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
                    currentNode.setStyle("-fx-background-color: white");

                    numberOfActions = StaticRunMe.loop(x, y, numberOfActions);
                    //TODO figure out a stopping criterion

                    // Get the current node and set its background back to red to mark the location of the agent
                    Node nextNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
                    nextNode.setStyle("-fx-background-color: red");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();


    }

    public void noGUI() {
        boolean stop = false;

        ArrayList<Integer> x = new ArrayList<Integer>();
        ArrayList<Integer> y = new ArrayList<Integer>();

        //keep learning until you decide to stop
        int numberOfActions = 0;
        Agent robot = StaticRunMe.robot;


        while (!stop) {
            // Get the current node and set its background back to white
            Node currentNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
            currentNode.setStyle("-fx-background-color: white");

            numberOfActions = StaticRunMe.loop(x, y, numberOfActions);
        }
    }

}
