package tudelft.rl.mysolution;

import tudelft.rl.Action;
import tudelft.rl.Agent;
import tudelft.rl.Maze;
import tudelft.rl.State;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;

public class StaticRunMe {

    public static Agent robot;
    public static Maze maze;
    private static MyEGreedy selection;
    private static MyQLearning learn;
    private static int width = 9, height = 9;

    public static void initEasyMaze() {
        //load the maze
        URL file = System.class.getResource("/data/easy_maze.txt");
        maze = new Maze(new File(file.getPath()));
        init();
    }

    public static void initToyMaze() {
        //load the maze
        URL file = System.class.getResource("/data/toy_maze.txt");
        maze = new Maze(new File(file.getPath()));
        init();
    }

    public static void init() {
        width = maze.getStates()[0].length - 1;
        height = maze.getStates().length - 1;
        //Set the reward at the bottom right to 10
        maze.setR(maze.getState(width, height), 10);

        //set The reward at the top right to 5
        //maze.setR(maze.getState(width, 0), 5);

        //create a robot at starting and reset location (0,0) (top left)
        robot = new Agent(0, 0);

        //make a selection object (you need to implement the methods in this class)
        selection = new MyEGreedy();

        //make a Qlearning object (you need to implement the methods in this class)
        learn = new MyQLearning();
    }

    public static double calculateEpsilon(double epsilon, int numberOfTrials, boolean useAdaptiveEpsilon) {
        if (useAdaptiveEpsilon) {
            // Calculate the adaptive epsilon
            double trialsDouble = numberOfTrials;

            double adaptiveEpsilon = Math.log10(10- (trialsDouble / 10));

            if (adaptiveEpsilon < 0.0) {
                adaptiveEpsilon = 0.0;
            }

            return adaptiveEpsilon;
        } else {
            return epsilon;
        }


    }


    public static int loop(ArrayList<Integer> x, ArrayList<Integer> y, double epsilon, double alfa, double gamma, int numberOfTrails) {
        //Store old state.
        State oldState = robot.getState(maze);

        //Decide which action the do.
        Action action = selection.getEGreedyAction(robot, maze, learn, calculateEpsilon(epsilon, numberOfTrails, false));
        //Do the action & store the new state.
        State newState = robot.doAction(action, maze);

        double r = maze.getR(newState);

        //Update the Q.
        learn.updateQ(oldState, action, r, newState, maze.getValidActions(robot), alfa, gamma);

        //Store the position
        x.add(robot.x);
        y.add(robot.y);
        
        if (robot.x == width && robot.y == height) {
            numberOfTrails++;
            System.out.print(robot.nrOfActionsSinceReset + " ");
            robot.reset();
        }
        return numberOfTrails;
    }

    private static void printPath(ArrayList<Integer> x, ArrayList<Integer> y) {
        for (int xx : x)
            System.out.println(xx);
        System.out.println("==================================");
        for (int yy : y)
            System.out.println(yy);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
