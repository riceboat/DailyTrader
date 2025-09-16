package dailyTrader;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import backTesting.Strategy;
import backTesting.StrategySimulator;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		ArrayList<Bars> data = new ArrayList<Bars>();
		ArrayList<String> tickers = new ArrayList<String>();
		tickers.add("AAPL");
		tickers.add("SPY");
		tickers.add("NVDA");
		tickers.add("TSLA");
		tickers.add("V");
		tickers.add("BAC");
		tickers.add("AMD");
		for (String ticker : tickers) {
			data.add(apiManager.getHistoricalBars(ticker, 365, ChronoUnit.DAYS));
		}
		Portfolio portfolio = new Portfolio(1000);
		StrategySimulator simulator = new StrategySimulator(new Strategy(), data, portfolio);
		simulator.run();
	}
}
