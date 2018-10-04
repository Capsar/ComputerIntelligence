package tudelft.rl.mysolution;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import tudelft.rl.*;

/**
 * Application that launches the maze, the training and initialization of the maze is done in the mazeController.
 */
public class RunMe extends Application {

    private static Scene scene;

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = new File("resources/maze.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        scene = new Scene(root, 1500, 800);

        primaryStage = primaryStage;
        primaryStage.setTitle("Maze");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static Scene getScene() {
        return scene;
    }
}
