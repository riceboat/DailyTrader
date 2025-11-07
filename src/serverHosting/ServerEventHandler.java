package serverHosting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import backTesting.StrategySimulator;
import dailyTrader.APIManager;
import dailyTrader.Bars;
import dailyTrader.JSONManager;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import strategies.*;

public class ServerEventHandler implements Runnable {
	int i = 0;
	private APIManager apiManager;
	private String responseString;
	private HttpExchange httpExchange;

	public ServerEventHandler(APIManager apiManager, HttpExchange httpExchange) {
		this.apiManager = apiManager;
		this.httpExchange = httpExchange;
	}

	static String readFile(String filePath) {
		try {
			return Files.readString(Paths.get(filePath));
		} catch (IOException e) {
			return null;
		}
	}

	String responseHandler(String uriString, String requestString) {
		if (uriString.equals("")) {
			return readFile("pages/index.html");
		} else if (uriString.equals("api")) {
			System.out.println("API CALL -> " + requestString);
			double startTime = System.nanoTime();
			String[] splitString = requestString.split("="); // seperate via equals
			String keyString = splitString[0];
			String valueString = splitString[1];
			String result = null;
			if (keyString.equals("strategy")) {
				result = runStrategy(valueString, 365);
			} else if (keyString.equals("portfolio")) {
				result = apiManager.getPortfolio(Integer.parseInt(valueString)).toJSON().toString();
			} else if (keyString.equals("topStocks")) {
				JSONArray tickerStringArray = new JSONArray(
						apiManager.getMostActiveSymbols(Integer.parseInt(valueString)));
				JSONObject resultObject = new JSONObject();
				resultObject.put("topStocks", tickerStringArray);
				result = resultObject.toString();
			} else if (keyString.equals("bars")) {
				result = apiManager.getHistoricalBars(valueString, 365, ChronoUnit.DAYS).toJSON().toString();
			} else if (keyString.equals("market")) {
				result = apiManager.getHistoricalBars(valueString, 365, ChronoUnit.DAYS).toJSON().toString();
			} else if (keyString.equals("strategyNames")) {
				
				JSONObject strategyNames = new JSONObject();
				JSONArray nameArray = new JSONArray();
				nameArray.put("MACDLongShort");
				nameArray.put("RandomActions");		
				nameArray.put("BuyAndHoldEverything");
				strategyNames.put("strategyNames", nameArray);
				result = strategyNames.toString();
			}
			double timeTaken = (System.nanoTime() - startTime) / 1000000;
			System.out.println(requestString + " took " + timeTaken + "ms");
			return result;
		} else {
			return readFile(uriString);
		}
	}

	@Override
	public void run() {
		String uriString = httpExchange.getRequestURI().toString().substring(1);
		String response = null;
		InputStream inputStream = httpExchange.getRequestBody();
		Scanner s = new Scanner(inputStream).useDelimiter("\\A");
		String requestString = s.hasNext() ? s.next() : "";
		s.close();
		try {
			if (httpExchange.getRequestMethod().equals("GET")) {
				response = responseHandler(uriString, requestString);
				if (response == null) {
					response = readFile("pages/404.html");
					httpExchange.sendResponseHeaders(404, response.length());
				} else {
					httpExchange.sendResponseHeaders(200, response.length());
				}
			} else if (httpExchange.getRequestMethod().equals("POST")) {
				response = responseHandler(uriString, requestString);
				httpExchange.sendResponseHeaders(200, response.length());
			}

			OutputStream os = httpExchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public String getJSONResponse() {
		return responseString;
	}

}
