import javafx.fxml.Initializable;
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
import java.util.ResourceBundle;

/**
 * Created by Sam van Berkel on 27/09/2018.
 */
public class mazeController implements Initializable{
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("controller");
        //launchMaze();
    }
}
