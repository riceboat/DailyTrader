package dailyTrader;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import strategies.*;
import backTesting.StrategySimulator;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		ArrayList<Bars> data = new ArrayList<Bars>();
		ArrayList<String> tickers = new ArrayList<String>();
		tickers.add("NVDA");
		tickers.add("TSLA");
		tickers.add("AAPL");
		tickers.add("AMD");
		tickers.add("GOOG");
		for (String ticker : tickers) {
			data.add(apiManager.getHistoricalBars(ticker, 365, ChronoUnit.DAYS));
		}
		Portfolio portfolio = apiManager.getPortfolio();
		Market market = new Market(data);
		Strategy strategy = new MACDBestSingleStock(24, 12, 9);
		StrategySimulator simulator = new StrategySimulator(strategy, market, portfolio);
		simulator.run();
	}
}
