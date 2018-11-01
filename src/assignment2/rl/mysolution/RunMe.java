package assignment2.rl.mysolution;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application that launches the maze, the training and initialization of the maze is done in the assignment2.mazeController.
 */
public class RunMe extends Application {

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = new File("resources/maze.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Maze");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
