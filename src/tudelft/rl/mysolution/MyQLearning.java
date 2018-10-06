package tudelft.rl.mysolution;

import java.util.ArrayList;

import tudelft.rl.Action;
import tudelft.rl.QLearning;
import tudelft.rl.State;

public class MyQLearning extends QLearning {

	@Override
	public void updateQ(State s, Action a, double r, State s_next, ArrayList<Action> possibleActions, double alfa, double gamma) {
		double oldQ = this.getQ(s, a);
		double maxQ = Double.MIN_VALUE;
		double[] actionValues = this.getActionValues(s_next, possibleActions);
		for(double value : actionValues) {
			if(value > maxQ)
				maxQ = value;
		}

		double newQ = oldQ + alfa * (r + gamma*maxQ-oldQ);
		this.setQ(s, a, newQ);
	}

}
