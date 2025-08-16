package dailyTrader;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		ArrayList<Bars> bars = new ArrayList<Bars>();
		ArrayList<String> tickers = apiManager.getMostActiveSymbols(25);
		for (String ticker : tickers) {
			System.out.print(ticker + ", ");
			bars.add(apiManager.getHistoricalBars(ticker, 30, ChronoUnit.DAYS));
		}
		CorrelationMatrix correlationMatrix = new CorrelationMatrix();
		Bars SPYBars = apiManager.getHistoricalBars("SPY", 30, ChronoUnit.DAYS);
		correlationMatrix = correlationMatrix.correlationWithSymbol(bars, SPYBars);
		System.out.println(correlationMatrix);

	}
}
