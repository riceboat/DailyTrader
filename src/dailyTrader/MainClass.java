package dailyTrader;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		ArrayList<Bars> bars = new ArrayList<Bars>();
		ArrayList<String> tickers = new ArrayList<String>();
		tickers.add("AAPL");
		tickers.add("SPY");
		tickers.add("NVDA");
		tickers.add("DJT");
		tickers.add("TSLA");
		tickers.add("V");
		tickers.add("BAC");
		tickers.add("AMD");
		tickers.add("QQQ");
		tickers.add("SQQQ");
		for (String ticker : tickers) {
			bars.add(apiManager.getHistoricalBars(ticker, 120, ChronoUnit.DAYS));
		}
		CorrelationMatrix correlationMatrix = new CorrelationMatrix();
		Bars SPYBars = apiManager.getHistoricalBars("SPY", 120, ChronoUnit.DAYS);
		correlationMatrix = correlationMatrix.correlationWithSymbol(bars, SPYBars);
		System.out.println(correlationMatrix);
		bars = new ArrayList<Bars>();
		for (String ticker : tickers) {
			bars.add(apiManager.getHistoricalBars(ticker, 30, ChronoUnit.DAYS));
		}
		correlationMatrix = new CorrelationMatrix();
		SPYBars = apiManager.getHistoricalBars("SPY", 30, ChronoUnit.DAYS);
		correlationMatrix = correlationMatrix.correlationWithSymbol(bars, SPYBars);
		System.out.println(correlationMatrix);

	}
}
