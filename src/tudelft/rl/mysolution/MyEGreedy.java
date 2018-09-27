package tudelft.rl.mysolution;

import tudelft.rl.Action;
import tudelft.rl.Agent;
import tudelft.rl.EGreedy;
import tudelft.rl.Maze;
import tudelft.rl.QLearning;

import java.util.ArrayList;
import java.util.Collections;

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

		double[] actionValues = q.getActionValues(r.getState(m), availableActions);

		double maximumValue = Double.MIN_VALUE;
		int maximumIndex = 0;

		for (int i = 0; i < actionValues.length; i++) {
			if (actionValues[i] > maximumValue) {
				maximumValue = actionValues[i];
				maximumIndex = i;
			}
		}
		return availableActions.get(maximumIndex);
	}

	@Override
	public Action getEGreedyAction(Agent r, Maze m, QLearning q, double epsilon) {
		epsilon = epsilon / r.nrOfActionsSinceReset + 1;

		double probability = (1 - epsilon);

		if (Math.random() <= probability) {
			return getBestAction(r, m, q);
		} else {
			return getRandomAction(r, m);
		}
	}

}
