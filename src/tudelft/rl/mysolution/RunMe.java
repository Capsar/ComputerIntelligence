package tudelft.rl.mysolution;

import java.io.File;
import java.net.URL;

import tudelft.rl.*;

public class RunMe {

	public static void main(String[] args) {

		//load the maze
		//TODO replace this with the location to your maze on your file system
		URL file = System.class.getResource("/toy_maze.txt");
		Maze maze = new Maze(new File(file.getPath()));

		//Set the reward at the bottom right to 10
		maze.setR(maze.getState(9, 9), 10);

		//create a robot at starting and reset location (0,0) (top left)
		Agent robot=new Agent(0,0);

		//make a selection object (you need to implement the methods in this class)
		EGreedy selection=new MyEGreedy();

		//make a Qlearning object (you need to implement the methods in this class)
		QLearning learn=new MyQLearning();

		boolean stop=false;

		//keep learning until you decide to stop
		while (!stop) {
			//TODO implement the action selection and learning cycle
			double epsilon = 0.5;
			Action action = selection.getEGreedyAction(robot, maze, learn, epsilon);
			robot.doAction(action, maze);
			State robotS = robot.getState(maze);

			//TODO figure out a stopping criterion
		}

	}

}
