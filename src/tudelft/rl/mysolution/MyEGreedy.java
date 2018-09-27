package tudelft.rl.mysolution;

import tudelft.rl.Action;
import tudelft.rl.Agent;
import tudelft.rl.EGreedy;
import tudelft.rl.Maze;
import tudelft.rl.QLearning;

public class MyEGreedy extends EGreedy {

	@Override
	public Action getRandomAction(Agent r, Maze m) {
		//TODO to select an action at random in State s
		return null;
	}

	@Override
	public Action getBestAction(Agent r, Maze m, QLearning q) {
		//TODO to select the best possible action currently known in State s.
		return null;
	}

	@Override
	public Action getEGreedyAction(Agent r, Maze m, QLearning q, double epsilon) {
		//TODO to select between random or best action selection based on epsilon.
		return null;
	}

}
