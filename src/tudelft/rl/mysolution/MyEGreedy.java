package tudelft.rl.mysolution;

import tudelft.rl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MyEGreedy extends EGreedy {

    @Override
    public Action getRandomAction(Agent r, Maze m) {
        ArrayList<Action> availableActions = m.getValidActions(r);

        Collections.shuffle(availableActions);

        return availableActions.get(0);
    }

    @Override
    public Action getBestAction(Agent r, Maze m, QLearning q) {
        ArrayList<Action> availableActions = m.getValidActions(r);
        Collections.shuffle(availableActions);
        State s = r.getState(m);

        double maximumValue = Double.MIN_VALUE;
        int maximumIndex = 0;
        for (int i = 0; i < availableActions.size(); i++) {
            Action a = availableActions.get(i);
            double sQ = q.getQ(s, a);
            if (sQ > maximumValue) {
                maximumValue = sQ;
                maximumIndex = i;
            }
        }
        return availableActions.get(maximumIndex);

    }

    @Override
    public Action getEGreedyAction(Agent r, Maze m, QLearning q, double epsilon) {
		epsilon = epsilon / ((r.nrOfActionsSinceReset + 1)/10);

        double probability = (1 - epsilon);

        if (new Random().nextDouble() <= probability) {
            return getBestAction(r, m, q);
        } else {
            return getRandomAction(r, m);
        }
    }

}
