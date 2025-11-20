package strategies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import backTesting.TradingAction;
import dailyTrader.Market;
import dailyTrader.Portfolio;

public interface Strategy {
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions, int day);

	public default String getName() {
		return this.getClass().getSimpleName();
	}
	
	public List<String> getParameterNames();

	public void setParameters(Map<String, String> parameterMap);
}
