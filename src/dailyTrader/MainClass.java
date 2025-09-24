package dailyTrader;

import java.util.ArrayList;

import strategies.*;
import backTesting.StrategySimulator;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);

		ArrayList<String> tickers = new ArrayList<String>();
		tickers.add("NVDA");
		tickers.add("TSLA");
		tickers.add("AAPL");
		tickers.add("AMD");
		tickers.add("GOOG");
		
		Portfolio portfolio = apiManager.getPortfolio();
		JSONManager jsonManager = new JSONManager();
		//Market market = apiManager.createMarketFromTickers(tickers);
		//jsonManager.writeToJsonFile(market, "market");
		Market market = jsonManager.readMarketFromFile("market.json");
		Strategy strategy = new MACDBestSingleStock(24, 12, 9);
		StrategySimulator simulator = new StrategySimulator(strategy, market, portfolio);
		simulator.run();
	}

}
