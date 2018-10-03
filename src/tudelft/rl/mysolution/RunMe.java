package tudelft.rl.mysolution;

import java.io.File;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application that launches the maze, the training and initialization of the maze is done in the mazeController.
 */
public class RunMe extends Application {

    private static Stage window;

	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL url = new File("resources/menu.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        window = primaryStage;
        window.setTitle("Maze");
        window.setScene(new Scene(root));
        primaryStage.show();
    }

    public static Stage getWindow() {
        return window;
    }
}
