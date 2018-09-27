package tudelft.rl.mysolution;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tudelft.rl.*;

public class RunMe extends Application {

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
			double r = 0.5;
			double alfa = 0.5;
			double gamma = 0.5;
			State oldState = robot.getState(maze);
			Action action = selection.getEGreedyAction(robot, maze, learn, epsilon);
			robot.doAction(action, maze);
			State newSate = robot.getState(maze);
			learn.updateQ(oldState, action, r, newSate, maze.getValidActions(robot), alfa, gamma);
			if(robot.x == 9 && robot.y == 9) {
				robot.reset();
			}
			//TODO figure out a stopping criterion
		}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println("test");
		Parent root = FXMLLoader.load(getClass().getResource("/src/tudelft/rl/mysolution/maze.fxml"));
		Scene scene = new Scene(root, 1000, 1000);
		primaryStage.setTitle("Maze");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
