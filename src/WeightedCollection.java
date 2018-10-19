import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * Created by Sam van Berkel on 11/10/2018.
 */
public class WeightedCollection {
    private ArrayList<Direction> directions;
    private ArrayList<Double> probabilities;
    private Random random;
    private double totalProbability;

    public WeightedCollection() {
        this.random = new Random();
        directions = new ArrayList<>();
        probabilities = new ArrayList<>();
    }

    public void add(Direction dir, double probability) {
        directions.add(dir);
        probabilities.add(totalProbability + probability);
        totalProbability += probability;
    }

    public Direction get() {
        double rand = random.nextDouble();

        double lowestDifference = Double.MAX_VALUE;
        Direction currentSelection = null;

        for (int i = 0; i < probabilities.size(); i++) {
            if (rand < probabilities.get(i) && probabilities.get(i) - rand < lowestDifference) {
                currentSelection = directions.get(i);
                lowestDifference = probabilities.get(i) - rand;
            }
        }

        return currentSelection;
    }

    public void reset() {
        directions = new ArrayList<>();
        probabilities = new ArrayList<>();
        totalProbability = 0;
    }
}
