package strategies;

import java.util.ArrayList;
import java.util.Random;

import backTesting.TradingAction;
import dailyTrader.Market;
import dailyTrader.Portfolio;

public class RandomActions extends Strategy {
	public RandomActions(double actionProbability) {
		setParameterValue("actionProbability", actionProbability);
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions) {
		double actionProbability = getParameterValue("actionProbability");
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		Random random = new Random();
		if (random.nextDouble() < actionProbability) {
			int ranInt = random.nextInt(possibleActions.size());
			TradingAction action = possibleActions.get(ranInt);
			action.setJustificationString("Our random threshold was exceeded, so we picked a random trading action");
			chosenActions.add(action);
		}
		return chosenActions;
	}

	@Override
	public int getDataCollectionPeriod() {
		return 0;
	}
}
