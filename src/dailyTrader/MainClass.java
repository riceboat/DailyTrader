package dailyTrader;

import java.util.ArrayList;

import strategies.*;
import backTesting.StrategySimulator;
import serverHosting.Server;
import serverHosting.ServerEventHandler;

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
		JSONManager jsonManager = new JSONManager();
		Portfolio portfolio = apiManager.getPortfolio();
		jsonManager.writeToJSONFile(portfolio, "data/portfolio");	
		//Market market = jsonManager.readMarketFromFile("market.json");
		//market = apiManager.createMarketFromTickers(tickers, 365 * 10);
		//jsonManager.writeToJSONFile(market, "data/market");
		Server server = new Server();
		ServerEventHandler eventHandler = new ServerEventHandler(apiManager);
		server.addEventHandler(eventHandler);
		server.startServer();
		
	}
}
