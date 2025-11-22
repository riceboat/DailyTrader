package serverHosting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import backTesting.SimulationResults;
import backTesting.StrategySimulator;
import dailyTrader.APIManager;
import dailyTrader.JSONManager;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import strategies.BuyAndHoldEverything;
import strategies.MACDLongShort;
import strategies.RandomActions;
import strategies.SMACrossover;
import strategies.Strategy;

public class ServerEventHandler implements Runnable {
	int i = 0;
	private APIManager apiManager;
	private String responseString;
	private HttpExchange httpExchange;
	private ArrayList<Strategy> strategyObjectList;

	public ServerEventHandler(APIManager apiManager, HttpExchange httpExchange) {
		this.apiManager = apiManager;
		this.httpExchange = httpExchange;
		strategyObjectList = new ArrayList<Strategy>();
		strategyObjectList.add(new BuyAndHoldEverything());
		strategyObjectList.add(new RandomActions(0));
		strategyObjectList.add(new MACDLongShort(0, 0, 0));
		strategyObjectList.add(new SMACrossover(0, 0));
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
			LinkedHashMap<String, String> requestStringHashMap = new LinkedHashMap<String, String>();
			String[] splitParamStrings = requestString.split("&");
			for (int i = 0; i < splitParamStrings.length; i++) {
				String[] splitString = splitParamStrings[i].split("="); // seperate via equals
				requestStringHashMap.put(splitString[0], splitString[1]);
			}
			String keyString = requestString.split("=")[0];
			String valueString = requestStringHashMap.get(keyString);
			String result = null;
			if (keyString.equals("strategy")) {
				LinkedHashMap<String, Double> convertedHashMap = new LinkedHashMap<String, Double>();
				for (Entry<String, String> entry : requestStringHashMap.entrySet()) {
					if (!keyString.equals(entry.getKey())) {
						convertedHashMap.put(entry.getKey(), Double.parseDouble(entry.getValue()));
					}
				}
				result = runStrategy(valueString, 365, convertedHashMap);
			} else if (keyString.equals("portfolio")) {
				result = apiManager.getPortfolio(Integer.parseInt(valueString)).toJSON().toString();
			} else if (keyString.equals("addTicker")) {
				apiManager.saveTicker(valueString);
				result = "";
			} else if (keyString.equals("removeTicker")) {
				apiManager.deleteTicker(valueString);
				result = "";
			} else if (keyString.equals("topStocks")) {
				JSONArray tickerStringArray = new JSONArray(
						apiManager.getMostActiveSymbols(Integer.parseInt(valueString)));
				JSONObject resultObject = new JSONObject();
				resultObject.put("topStocks", tickerStringArray);
				result = resultObject.toString();
			} else if (keyString.equals("savedTickers")) {
				JSONArray tickerStringArray = new JSONArray(apiManager.getSavedTickers());
				JSONObject resultObject = new JSONObject();
				resultObject.put("tickers", tickerStringArray);
				result = resultObject.toString();
			} else if (keyString.equals("bars")) {
				result = apiManager.getHistoricalBars(valueString, 365, ChronoUnit.DAYS).toJSON().toString();
			} else if (keyString.equals("market")) {
				result = apiManager.createMarketFromTickers(apiManager.getSavedTickers(), 365).toJSON().toString();
			} else if (keyString.equals("strategyNames")) {
				JSONObject strategyNames = new JSONObject();
				JSONArray nameArray = new JSONArray();
				for (Strategy strategy : strategyObjectList) {
					JSONObject strategyParameterJsonObject = new JSONObject();
					strategyParameterJsonObject.put("name", strategy.getName());
					strategyParameterJsonObject.put("parameters", strategy.getParameterNames());
					nameArray.put(strategyParameterJsonObject);
				}
				strategyNames.put("strategies", nameArray);
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

	public String runStrategy(String strategyString, int numDays, LinkedHashMap<String, Double> parameterMap) {
		Strategy strategy = new BuyAndHoldEverything();
		for (Strategy strat : strategyObjectList) {
			if (strat.getName().equals(strategyString)) {
				strategy = strat;
				strategy.setParameters(parameterMap);
			}
		}
		Portfolio portfolio = apiManager.getPortfolio(numDays);
		JSONManager jsonManager = new JSONManager();

		Market market = apiManager.createMarketFromTickers(apiManager.getSavedTickers(), numDays);
		StrategySimulator simulator = new StrategySimulator(strategy, market, portfolio, false);
		SimulationResults simulationResults = simulator.run();
		return jsonManager.toJSONString(simulationResults);
	}

	public String getJSONResponse() {
		return responseString;
	}

}
