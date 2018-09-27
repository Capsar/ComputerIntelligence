import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import tudelft.rl.Action;
import tudelft.rl.Agent;
import tudelft.rl.EGreedy;
import tudelft.rl.Maze;
import tudelft.rl.QLearning;
import tudelft.rl.State;
import tudelft.rl.mysolution.MyEGreedy;
import tudelft.rl.mysolution.MyQLearning;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by Sam van Berkel on 27/09/2018.
 */
public class mazeController implements Initializable{
    Maze maze;
    Agent robot;
    EGreedy selection;
    QLearning learn;


    @FXML
    private GridPane mazeGridPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeMaze();
        createMazeGrid();
    }

    /**
     * Creates the grid for the maze in the gui, loads the walls with black and the paths with white.
     */
    public void createMazeGrid() {
        State[][] states = maze.getStates();
        int mazeWidth = states[0].length;
        int mazeHeight = states.length;

        for (int i = 0; i < mazeWidth; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100 / mazeWidth);
            mazeGridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < mazeHeight; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100 / mazeWidth);
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
     * Initializes the global variables of the maze and agent.
     */
    public void initializeMaze() {
        //load the maze
        URL file = System.class.getResource("/data/toy_maze.txt");
        maze = new Maze(new File(file.getPath()));

        //Set the reward at the bottom right to 10
        maze.setR(maze.getState(9, 9), 10);

        //create a robot at starting and reset location (0,0) (top left)
        robot = new Agent(0,0);

        //make a selection object (you need to implement the methods in this class)
        selection = new MyEGreedy();

        //make a Qlearning object (you need to implement the methods in this class)
        learn = new MyQLearning();
    }

    /**
     * Starts the agent in the maze on a new thread.
     * @throws InterruptedException
     */
    @FXML
    public void launchMaze() throws InterruptedException {
        Thread thread = new Thread(){
            public void run(){
                boolean stop=false;

                ArrayList<Integer> x = new ArrayList<Integer>();
                ArrayList<Integer> y = new ArrayList<Integer>();

                //keep learning until you decide to stop
                int numberOfActions = 0;

                while (!stop) {
                    // Get the current node and set its background back to white
                    Node currentNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
                    currentNode.setStyle("-fx-background-color: white");

                    numberOfActions++;
                    double epsilon = 0.1;
                    double r = 0.5;
                    double alfa = 0.5;
                    double gamma = 0.5;
                    //Store old state.
                    tudelft.rl.State oldState = robot.getState(maze);
                    //Decide which action the do.
                    Action action = selection.getEGreedyAction(robot, maze, learn, epsilon);
                    //Do the action.
                    robot.doAction(action, maze);
                    //Store the new state.
                    tudelft.rl.State newSate = robot.getState(maze);

                    //Update the Q.
                    learn.updateQ(oldState, action, r, newSate, maze.getValidActions(robot), alfa, gamma);

                    //Store the position
                    x.add(robot.x);
                    y.add(robot.y);
                    if(robot.x == 9 && robot.y == 9) {
                        for(int xx : x)
                            System.out.println(xx);
                        System.out.println("==================================");
                        for(int yy : y)
                            System.out.println(yy);
                        System.out.println("goal reached");
                        robot.reset();
                    }
                    //TODO figure out a stopping criterion

                    // Get the current node and set its background back to red to mark the location of the agent
                    Node nextNode = getNodeByRowColumnIndex(robot.getY(), robot.getX(), mazeGridPane);
                    nextNode.setStyle("-fx-background-color: red");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();



    }

    /**
     * Returns the node at a specific row and column of a gridpane.
     * @param row the row where the node is
     * @param column the column where the node is
     * @param gridPane the gridpane that contains the node
     * @return the node at the given location
     */
    public static Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();

        for (Node node : childrens) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }

        return result;
    }
}
