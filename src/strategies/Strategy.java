package strategies;

import java.util.ArrayList;
import java.util.List;

import backTesting.TradingAction;
import dailyTrader.Bars;
import dailyTrader.Market;
import dailyTrader.Portfolio;

public interface Strategy {

	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions, int day);
}
