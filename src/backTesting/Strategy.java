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
	
	public TradingAction decide(List<Bars> data, Portfolio portfolio, ArrayList<TradingAction> possibleActions, int day) {
		TradingAction chosenAction = null;
		if (portfolio.cash > 0) {
			for (TradingAction action : possibleActions) {
				if (action.side == Side.SHORT) {
					chosenAction = action;
				}
			}
		}
		else {
			for (TradingAction action : possibleActions) {
				if (action.side == Side.HOLD) {
					chosenAction = action;
				}
			}
		}
		return chosenAction;
	}
}
