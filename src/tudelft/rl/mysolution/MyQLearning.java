package tudelft.rl.mysolution;

import java.util.ArrayList;

import tudelft.rl.Action;
import tudelft.rl.QLearning;
import tudelft.rl.State;

public class MyQLearning extends QLearning {

	@Override
	public void updateQ(State s, Action a, double r, State s_next, ArrayList<Action> possibleActions, double alfa, double gamma) {
		double Qmax = Double.MIN_VALUE;
		for(Action action : possibleActions) {
			if (this.getQ(s_next, action) > Qmax) {
				Qmax = this.getQ(s_next, action);
			}
		}

		double newQ = this.getQ(s, a) + alfa * (r + gamma*Qmax-this.getQ(s,a));

		if (newQ < 0.00000001) {
			newQ = 0;
		}

		if (newQ != this.getQ(s, a)) {
			System.out.println("updated q: " + this.getQ(s, a) + " with: " + newQ);
		}

		this.setQ(s, a, newQ);
	}

}
