package tudelft.rl.mysolution;

import tudelft.rl.Action;
import tudelft.rl.Agent;
import tudelft.rl.Maze;
import tudelft.rl.State;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class StaticRunMe {

    public static Agent robot;
    public static Maze maze;
    private static MyEGreedy selection;
    private static MyQLearning learn;

    public static void init() {
        //load the maze
        URL file = System.class.getResource("/data/toy_maze.txt");
        maze = new Maze(new File(file.getPath()));

        //Set the reward at the bottom right to 10
        maze.setR(maze.getState(9, 9), 10);

        //create a robot at starting and reset location (0,0) (top left)
        robot = new Agent(0, 0);

        //make a selection object (you need to implement the methods in this class)
        selection = new MyEGreedy();

        //make a Qlearning object (you need to implement the methods in this class)
        learn = new MyQLearning();
    }


    public static int loop(ArrayList<Integer> x, ArrayList<Integer> y, int numberOfActions) {
        numberOfActions++;
        double epsilon = 0.1;
        double r = 0.9;
        double alfa = 0.9;
        double gamma = 0.9;
        //Store old state.
        State oldState = robot.getState(maze);
        //Decide which action the do.
        Action action = selection.getEGreedyAction(robot, maze, learn, epsilon);
        //Do the action.
        robot.doAction(action, maze);
        //Store the new state.
        State newSate = robot.getState(maze);

        //Update the Q.
        learn.updateQ(oldState, action, r, newSate, maze.getValidActions(robot), alfa, gamma);

        //Store the position
        x.add(robot.x);
        y.add(robot.y);
        if (robot.x == 9 && robot.y == 9) {
//                        printPath(x, y);
            System.out.println("goal reached");
            robot.reset();
        }
        return numberOfActions;
    }

    private static void printPath(ArrayList<Integer> x, ArrayList<Integer> y) {
        for(int xx : x)
            System.out.println(xx);
        System.out.println("==================================");
        for(int yy : y)
            System.out.println(yy);
    }


}
