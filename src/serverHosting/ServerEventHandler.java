package serverHosting;

import java.time.temporal.ChronoUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import backTesting.StrategySimulator;
import dailyTrader.APIManager;
import dailyTrader.Bars;
import dailyTrader.JSONManager;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import strategies.*;

public class ServerEventHandler {
	int i = 0;
	APIManager apiManager;

	public ServerEventHandler(APIManager apiManager) {
		this.apiManager = apiManager;
	}

	public String call(String requestString) {
		String[] splitString = requestString.split("="); // seperate via equals
		System.out.println(requestString);
		String keyString = splitString[0];
		String valueString = splitString[1];
		String result = null;
		if (keyString.equals("strategy")) {
			result = runStrategy(valueString, 365);
		} else if (keyString.equals("portfolio")) {
			result = getPortfolio(Integer.parseInt(valueString));
		}
		else if (keyString.equals("topStocks")) {
			JSONArray tickerStringArray = new JSONArray(apiManager.getMostActiveSymbols(Integer.parseInt(valueString)));
			JSONObject resultObject = new JSONObject();
			resultObject.put("topStocks", tickerStringArray);
			result = resultObject.toString();
		}
		else if (keyString.equals("bars")) {
			result = apiManager.getHistoricalBars(valueString, 365, ChronoUnit.DAYS).toJSON().toString();
		}
		else if (keyString.equals("strategyNames")) {
			JSONObject strategyNames = new JSONObject();
			JSONArray nameArray = new JSONArray();
			nameArray.put("MACDLongShort");
			nameArray.put("RandomActions");
			nameArray.put("BuyAndHoldEverything");
			strategyNames.put("strategyNames", nameArray);
			result = strategyNames.toString();
		}
		return result;
	}

	public String getPortfolio(int numDays) {
		Portfolio portfolio = apiManager.getPortfolio(numDays);
		JSONManager jsonManager = new JSONManager();
		return jsonManager.toJSONString(portfolio);
	}

	public String runStrategy(String strategyString, int numDays) {
		Strategy strategy = new BuyAndHoldEverything();
		if (strategyString.equals("MACDLongShort")) {
			strategy = new MACDLongShort(12, 24, 9);
		} else if (strategyString.equals("BuyAndHoldEverything")) {
			strategy = new BuyAndHoldEverything();
		} else if (strategyString.equals("RandomActions")) {
			strategy = new RandomActions(0.25);
		}
		Portfolio portfolio = apiManager.getPortfolio(numDays);
		JSONManager jsonManager = new JSONManager();
		
		Market market = apiManager.createMarketFromTickers(apiManager.getMostActiveSymbols(6), numDays);
		StrategySimulator simulator = new StrategySimulator(strategy, market, portfolio, false);
		Bars strategyRunBars = simulator.run();
		return jsonManager.toJSONString(strategyRunBars);
	}

}
