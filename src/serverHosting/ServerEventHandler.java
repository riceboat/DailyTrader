package serverHosting;

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

	public void call(String requestString) {
		System.out.println(requestString);
		String[] splitString = requestString.split("="); // seperate via equals
		String keyString = splitString[0];
		String valueString = splitString[1];
		if (keyString.equals("strategy")) {
			runStrategy(valueString);
		}
	}

	public void runStrategy(String strategyString) {
		Strategy strategy = new BuyAndHoldEverything();
		if (strategyString.equals("MACDLongShort")) {
			strategy = new MACDLongShort(12, 24, 9);
		} else if (strategyString.equals("BuyAndHoldEverything")) {
			strategy = new BuyAndHoldEverything();
		}
		else if (strategyString.equals("RandomActions")) {
			strategy = new RandomActions(0.25);
		}
		Portfolio portfolio = apiManager.getPortfolio();
		JSONManager jsonManager = new JSONManager();
		jsonManager.writeToJSONFile(portfolio, "data/portfolio");
		Market market = jsonManager.readMarketFromFile("data/market.json");
		StrategySimulator simulator = new StrategySimulator(strategy, market, portfolio, false);
		Bars strategyRunBars = simulator.run();
		jsonManager.writeToJSONFile(strategyRunBars, "data/lastStrategy");
	}

}
