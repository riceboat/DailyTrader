package strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
			int ranInt = random.nextInt(possibleActions.size());
			chosenActions.add(possibleActions.get(ranInt));
		}
		return chosenActions;
	}

	@Override
	public List<String> getParameterNames() {
		ArrayList<String> nameStrings = new ArrayList<String>();
		nameStrings.add("actionProbability");
		return nameStrings;
	}

	@Override
	public void setParameters(Map<String, String> parameterMap) {
		this.actionProbability = Double.parseDouble(parameterMap.get("actionProbability"));
	}
}
