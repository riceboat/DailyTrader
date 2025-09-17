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
		tickers.add("NVDA");
		for (String ticker : tickers) {
			data.add(apiManager.getHistoricalBars(ticker, 365, ChronoUnit.DAYS));
		}
		Portfolio portfolio = apiManager.getPortfolio();
		Market market = new Market(data);
		StrategySimulator simulator = new StrategySimulator(new Strategy(), market, portfolio);
		simulator.run();
	}
}
