package assignment1;

import java.io.Serializable;

/**
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Connection implements Serializable {
    private Neuron start;
    private Neuron end;
    private double weight;

    public Connection(Neuron start, Neuron end, double minInitialWeight, double maxInitialWeight) {
        this.start = start;
        this.end = end;
        this.weight = minInitialWeight + Math.random() * (maxInitialWeight - minInitialWeight);
    }

    public Neuron getStart() {
        return start;
    }

    public Neuron getEnd() {
        return end;
    }

    public double getWeight() {
        return weight;
    }

    public void adjustWeight() {
        double deltaWeight = start.getLearningRate() * start.getLastOutput() * end.getLastErrorGradient();
        weight += deltaWeight;
    }
}
