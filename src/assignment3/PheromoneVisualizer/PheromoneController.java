package assignment3.PheromoneVisualizer;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the pheromone.fxml file
 * Created by Sam van Berkel on 25/10/2018.
 */
public class PheromoneController implements Initializable {
    double[][] pheromones;
    double maxPheromone;

    @FXML
    GridPane mazeGridPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        readPheromoneFile();
        loadPheromonesInGridPane();
    }

    /**
     * Loads the right pheromone numbers into the gridpane
     */
    public void loadPheromonesInGridPane() {
        mazeGridPane.setPrefWidth(4000);
        mazeGridPane.setPrefHeight(4000);

        for (int i = 0; i < pheromones[0].length; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100 / pheromones[0].length);
            mazeGridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i < pheromones.length; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100 / pheromones.length);
            mazeGridPane.getRowConstraints().add(rowConst);
        }

        for (int i = 0; i < pheromones[0].length; i++) {
            for (int j = 0; j < pheromones.length; j++) {
                double pheromone = pheromones[i][j];

                HBox hbox = new HBox();
                hbox.setAlignment(Pos.CENTER);
                hbox.setStyle("-fx-background-color: " + createHex(pheromone));

                if (pheromone != -1) {
                    Label label = new Label();
                    label.setText(String.valueOf(pheromone));
                    label.setStyle("-fx-font-size:9px;");
                    hbox.getChildren().add(label);
                }

                mazeGridPane.add(hbox, i, j);
            }
        }

        mazeGridPane.setStyle("-fx-border-color: blue");

    }

    /**
     * Creates a hex string with the right color for the amount of pheromone.
     * @param pheromone the amount of pheromone in the current box
     * @return hex representation of the color
     */
    public String createHex(double pheromone) {
        if (pheromone == 0) {
            return "#ffffff";
        } else if (pheromone == -1) {
            return "#000000";
        }

        float minHue = 0.3f;
        float maxHue = 0.6f;

        double amount = minHue + (pheromone / maxPheromone * (maxHue - minHue));
        float hue = (float) amount;

        Color color = Color.getHSBColor(hue, 1, 1f);

        String hex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());

        return hex;
    }

    /**
     * Read a text file with the amounts of pheromone and fill the two dimensional pheromone array
     */
    public void readPheromoneFile() {
        File file = new File("data/pheromones.txt");

        maxPheromone = Double.MIN_VALUE;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String firstLine = br.readLine();

            System.out.println(firstLine);

            String[] lineSplit = firstLine.split(" ");

            int width = Integer.valueOf(lineSplit[0]);
            int height = Integer.valueOf(lineSplit[1]);

            pheromones = new double[width][height];

            for (int i = 0; i < height; i++) {
                String currentLine = br.readLine();
                String[] valueStrings = currentLine.split(" ");

                for (int j = 0; j < valueStrings.length; j++) {
                    double value = Double.valueOf(valueStrings[j]);
                    pheromones[j][i] = value;
                    if (value > maxPheromone) {
                        maxPheromone = value;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
