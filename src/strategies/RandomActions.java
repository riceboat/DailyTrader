package strategies;

import java.util.ArrayList;
import java.util.Random;

import backTesting.TradingAction;
import dailyTrader.Market;
import dailyTrader.Portfolio;

public class RandomActions implements Strategy {
	double actionProbability;

	public RandomActions(double actionProbability) {
		this.actionProbability = actionProbability;
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio, ArrayList<TradingAction> possibleActions,
			int day) {
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		Random random = new Random();
		if (random.nextDouble() < actionProbability) {
			chosenActions.add(possibleActions.get(random.nextInt(possibleActions.size() - 1)));
		}
		return chosenActions;
	}
}
