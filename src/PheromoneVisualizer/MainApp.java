package PheromoneVisualizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

/**
 * Created by Sam van Berkel on 25/10/2018.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = new File("src/PheromoneVisualizer/pheromone.fxml").toURL();
        Parent root = FXMLLoader.load(url);

        Scene scene = new Scene(root, 1000, 1000);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
