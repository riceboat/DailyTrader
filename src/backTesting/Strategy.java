package backTesting;

import java.util.ArrayList;
import java.util.List;

import dailyTrader.Bars;
import dailyTrader.Portfolio;
import dailyTrader.Side;


public class Strategy {
	int startDay = 0;
	public Strategy() {
		
	}
	
	public ArrayList<TradingAction> decide(List<Bars> data, Portfolio portfolio, ArrayList<TradingAction> possibleActions, int day) {
		ArrayList<TradingAction>  chosenActions = new ArrayList<TradingAction>();
		if (portfolio.cash > 0) {
			for (TradingAction action : possibleActions) {
				if (action.side == Side.LONG) {
					chosenActions.add(action);
				}
			}
		}
		else {
			for (TradingAction action : possibleActions) {
				if (action.side == Side.HOLD) {
					chosenActions.add(action);
				}
			}
		}
		return chosenActions;
	}
}
