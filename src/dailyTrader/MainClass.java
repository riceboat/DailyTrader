package dailyTrader;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import strategies.*;
import backTesting.StrategySimulator;
import serverHosting.Server;

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

		//Portfolio portfolio = apiManager.getPortfolio();
		JSONManager jsonManager = new JSONManager();
		//Market market = apiManager.createMarketFromTickers(tickers);
		jsonManager.writeToJSONFile(apiManager.getHistoricalBars("NVDA", 30, ChronoUnit.HOURS), "data/NVDA30");
		jsonManager.writeToJSONFile(apiManager.getHistoricalBars("NVDA", 60, ChronoUnit.HOURS), "data/NVDA60");
		//Market market = jsonManager.readMarketFromFile("market.json");
		//Strategy strategy = new MACDBestSingleStock(24, 12, 9);
		//strategy = new BuyAndHoldEverything();
		//StrategySimulator simulator = new StrategySimulator(strategy, market, portfolio);
		//simulator.run();
		Server server = new Server();
		server.startServer();
	}

}
