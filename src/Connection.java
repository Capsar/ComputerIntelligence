/**
 * Created by Sam van Berkel on 12/09/2018.
 */
public class Connection {
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

    public void setStart(Neuron start) {
        this.start = start;
    }

    public Neuron getEnd() {
        return end;
    }

    public void setEnd(Neuron end) {
        this.end = end;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void adjustWeight() {
        double deltaWeight = start.getLearningRate() * start.getLastOutput() * end.getLastErrorGradient();

        //System.out.println("delta weight: " + deltaWeight);
        //System.out.println("lr: " + start.getLearningRate());
        //System.out.println("lo: " + start.getLastOutput());
        //System.out.println("leg: " + end.getLastErrorGradient());
        weight += deltaWeight;
    }
}
