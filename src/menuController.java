import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import tudelft.rl.mysolution.RunMe;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Sam van Berkel on 27/09/2018.
 */
public class menuController implements Initializable{
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void yesButton() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("maze.fxml"));
        RunMe.getWindow().setScene(new Scene(root));
    }

    @FXML
    public void noButton() {
        //mazeController.launchMaze();
    }
}
