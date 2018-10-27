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
    private double totalProbability;

    public WeightedCollection() {
        directions = new ArrayList<>();
        probabilities = new ArrayList<>();
        totalProbability = 0;
    }

    public static void main(String... args) {
        WeightedCollection collection = new WeightedCollection();
        collection.add(Direction.North, 0.3333);
        collection.add(Direction.East, 0.3333);
        collection.add(Direction.South, 0.3333);

        double size = 1000000;

        Direction[] list = new Direction[(int) size];
        for(int i = 0; i < size; i++)
            list[i] = collection.get();

        int north = 0;
        int east = 0;
        int south = 0;
        int west = 0;

        for(Direction dir : list) {
            switch(Direction.dirToInt(dir)) {
                case 0:
                    east++;
                    break;
                case 1:
                    north++;
                    break;
                case 2:
                    west++;
                    break;
                case 3:
                    south++;
                    break;
            }
        }

        System.out.println("North: " + (north/size));
        System.out.println("East: " + (east/size));
        System.out.println("South: " + (south/size));
        System.out.println("West: " + (west/size));


    }

    public void add(Direction dir, double probability) {
        directions.add(dir);
        probabilities.add(totalProbability + probability);
        totalProbability += probability;
    }

    public Direction get() {
        double rand = new Random().nextDouble();

        double lowestDifference = Double.MAX_VALUE;
        Direction currentSelection = null;

        for (int i = 0; i < probabilities.size(); i++) {
            if (rand < probabilities.get(i) && Math.abs(probabilities.get(i) - rand) < lowestDifference) {
                currentSelection = directions.get(i);
                lowestDifference = Math.abs(probabilities.get(i) - rand);
            }
        }

        return currentSelection;
    }

    public int size() {
        return directions.size();
    }

    public void reset() {
        directions = new ArrayList<>();
        probabilities = new ArrayList<>();
        totalProbability = 0;
    }
}
