package strategies;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import backTesting.TradingAction;
import dailyTrader.Market;
import dailyTrader.Portfolio;

public abstract class Strategy {
	private LinkedHashMap<String, Double> parameterMap;

	public Strategy() {
		parameterMap = new LinkedHashMap<String, Double>();
	}

	public abstract ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions, int day);

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public ArrayList<String> getParameterNames() {
		return new ArrayList<String>(parameterMap.keySet());
	}

	public void setParameters(LinkedHashMap<String, Double> parameterMap) {
		this.parameterMap = parameterMap;
	}

	public void setParameterValue(String parameterName, double parameterValue) {
		parameterMap.put(parameterName, parameterValue);
	}

	public double getParameterValue(String parameterName) {
		return parameterMap.get(parameterName);
	}

	// how many days of data do we need to collect before we start making decisions?
	public abstract int getDataCollectionPeriod();
}
