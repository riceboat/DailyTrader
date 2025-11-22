package strategies;

import java.util.ArrayList;
import backTesting.TradingAction;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Side;

public class BuyAndHoldEverything extends Strategy {
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio, ArrayList<TradingAction> possibleActions,
			int day) {
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		if (portfolio.getCash() > 0 && day == 0) {
			for (TradingAction action : possibleActions) {
				if (action.getSide() == Side.LONG) {
					action.setPercentage(1.0);
					chosenActions.add(action);
				}
			}
		} else {
			for (TradingAction action : possibleActions) {
				if (action.getSide() == Side.HOLD) {
					chosenActions.add(action);
				}
			}
		}
		return chosenActions;
	}
}
