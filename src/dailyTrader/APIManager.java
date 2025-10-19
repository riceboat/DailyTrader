package dailyTrader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.*;

public class APIManager {
	String public_key;
	String private_key;
	boolean paper;

	public APIManager(String public_key, String private_key, boolean paper) {
		this.public_key = public_key;
		this.private_key = private_key;
		this.paper = paper;
	}

	public float getAskPrice(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/quotes/latest", args, "data", "GET");
		float price = Float.parseFloat(response.getJSONObject("quotes").getJSONObject(symbol).get("bp").toString());
		return price;
	}

	public float getBidPrice(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/quotes/latest", args, "data", "GET");
		float price = Float.parseFloat(response.getJSONObject("quotes").getJSONObject(symbol).get("bp").toString());
		return price;
	}

	public Date getMarketTime() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = (JSONObject) APIRequest("v2/clock", args, "api", "GET");
		return stringToLocalDate(response.get("timestamp").toString());
	}

	public Date getNextMarketOpen() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = (JSONObject) APIRequest("v2/clock", args, "api", "GET");
		return stringToLocalDate(response.get("next_open").toString());
	}

	public Date getNextMarketClose() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = (JSONObject) APIRequest("v2/clock", args, "api", "GET");
		return stringToLocalDate(response.get("next_close").toString());
	}

	public boolean isMarketOpen() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = (JSONObject) APIRequest("v2/clock", args, "api", "GET");
		return response.get("is_open").toString() == "true";
	}

	public Date stringToLocalDate(String s) {
		return Date.from(Instant.parse(s));
	}

	public ArrayList<Article> getMostRecentNewsToday(String[] symbols) {
		List<String> symbolsArrayList = Arrays.asList(symbols);
		HashMap<String, String> args = new HashMap<String, String>();
		ArrayList<Article> articles = new ArrayList<Article>();
		String s = "";
		for (int i = 0; i < symbolsArrayList.size(); i++) {
			s += symbolsArrayList.get(i);
			if (i != symbolsArrayList.size() - 1) {
				s += "%2C";
			}
		}
		args.put("symbols", s);
		args.put("limit", "50");
		args.put("include_content", "true");
		args.put("exclude_contentless", "true");
		JSONObject response = (JSONObject) APIRequest("v1beta1/news", args, "data", "GET");
		JSONArray array = response.getJSONArray("news");
		for (int i = 0; i < array.length(); i++) {
			articles.add(new Article(array.getJSONObject(i)));
		}
		return articles;
	}

	public ArrayList<String> getMostActiveSymbols(int amount) {
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("by", "volume");
		args.put("top", Integer.toString(amount));
		JSONObject response = (JSONObject) APIRequest("v1beta1/screener/stocks/most-actives", args, "data", "GET");
		JSONArray actives = response.getJSONArray("most_actives");
		for (int i = 0; i < actives.length(); i++) {
			symbols.add(actives.getJSONObject(i).getString("symbol"));
		}
		return symbols;
	}

	public ArrayList<Order> getOpenOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("status", "open");
		JSONArray response = (JSONArray) APIRequest("v2/orders", args, "api", "GET");
		for (int i = 0; i < response.length(); i++) {
			orders.add(new Order(response.getJSONObject(i)));
		}
		return orders;
	}

	public ArrayList<Order> getAllOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("status", "all");
		JSONArray response = (JSONArray) APIRequest("v2/orders", args, "api", "GET");
		for (int i = 0; i < response.length(); i++) {
			orders.add(new Order(response.getJSONObject(i)));
		}
		return orders;
	}
	public Bars getPortfolioHistory(int numDays) {
		Bars portfolioHistoryBars = new Bars();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("timeframe", "1D");
		args.put("period", "1A");
		JSONObject response = (JSONObject) APIRequest("v2/account/portfolio/history", args, "api", "GET");
		JSONArray equityArray = response.getJSONArray("equity");
		JSONArray timestampArray = response.getJSONArray("timestamp");
		for (int i = 0; i < equityArray.length(); i++) {
			int unixEpoch = timestampArray.getInt(i);
			Date startDate = new Date((long)unixEpoch * 1000);
			Date endDate = new Date((long)unixEpoch * 1000);
			endDate.setDate(endDate.getDate() + 1);
			Bar bar = new Bar("history",equityArray.getDouble(i), startDate, endDate);
			portfolioHistoryBars.add(bar);
		}
		return portfolioHistoryBars;
	}
	public Portfolio getPortfolio(int numDays) {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONArray response = (JSONArray) APIRequest("v2/positions", args, "api", "GET");
		
		Bars portfolioHistoryBars = getPortfolioHistory(numDays);
		Portfolio portfolio = new Portfolio(portfolioHistoryBars);
		for (int i = 0; i < response.length(); i++) {
			portfolio.addPosition(new Position(response.getJSONObject(i)));
		}
		return portfolio;
	}

	public void closeAllPositions() {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("cancel_orders", "true");
		APIRequest("v2/positions", args, "api", "DELETE");
	}

	public JSONObject createOrder(String symbol, double d, String side) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbol", symbol);
		args.put("qty", Double.toString(d));
		args.put("side", side);
		args.put("type", "market");
		args.put("time_in_force", "day");
		JSONObject response = (JSONObject) APIRequest("v2/orders", args, "api", "POST");
		return response;
	}

	public Option getOptionByCode(String code) {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = (JSONObject) APIRequest("v2/options/contracts/" + code, args, "api", "GET");
		return new Option(response);

	}

	public OptionChain getOptions(String symbol) {
		ArrayList<Option> options = new ArrayList<Option>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("underlying_symbols", symbol);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(Date.from(Instant.now()));
		args.put("expiration_date_gte", dateString);
		JSONObject response = (JSONObject) APIRequest("v2/options/contracts", args, "api", "GET");
		JSONArray array = response.getJSONArray("option_contracts");
		for (int i = 0; i < array.length(); i++) {
			options.add(new Option(array.getJSONObject(i)));
		}
		OptionChain chain = new OptionChain(options, this);
		return chain;
	}

	public OptionChain getOptionsInRange(String symbol, int low, int high, int days) {
		ArrayList<Option> options = new ArrayList<Option>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("underlying_symbols", symbol);
		args.put("strike_price_gte", Integer.toString(low));
		args.put("strike_price_lte", Integer.toString(high));
		args.put("limit", "10000");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateStringGTE = formatter.format(Date.from(Instant.now()));
		args.put("expiration_date_gte", dateStringGTE);
		String dateStringLTE = formatter.format(Date.from(Instant.now().plus(days, ChronoUnit.DAYS)));
		args.put("expiration_date_lte", dateStringLTE);
		JSONObject response = (JSONObject) APIRequest("v2/options/contracts", args, "api", "GET");
		JSONArray array = response.getJSONArray("option_contracts");
		for (int i = 0; i < array.length(); i++) {
			options.add(new Option(array.getJSONObject(i)));
		}
		if (options.size() > 0) {
			OptionChain chain = new OptionChain(options, this);
			return chain;
		} else {
			return null;
		}

	}

	public Option getOptionQuote(Option o) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", o.symbol);
		JSONObject response = (JSONObject) APIRequest("v1beta1/options/quotes/latest", args, "data", "GET");
		JSONObject obj = response.getJSONObject("quotes").getJSONObject(o.symbol);

		float askPrice = obj.getFloat("ap");
		float bidPrice = obj.getFloat("bp");
		Date lastQuote = Date.from(Instant.parse(obj.getString("t")));
		double closePrice = getAskPrice(o.underlyingSymbol);
		Bars bars = getHistoricalBars(o.underlyingSymbol, 30, ChronoUnit.DAYS);
		o.updateFromQuote(askPrice, bidPrice, lastQuote, closePrice, bars);
		return o;

	}

	public ArrayList<Option> getOptionQuotes(ArrayList<Option> options) {
		HashMap<String, String> args = new HashMap<String, String>();

		int count = 0;
		while (count != options.size()) {
			String s = "";
			int limit = Math.min(100, options.size() - count);
			for (int i = 0; i < limit; i++) {
				s += options.get(i + count).symbol;
				if (i != limit - 1) {
					s += "%2C";
				}
			}
			args.put("symbols", s);
			JSONObject response = (JSONObject) APIRequest("v1beta1/options/quotes/latest", args, "data", "GET");
			JSONObject arr = response.getJSONObject("quotes");
			Bars bars = getHistoricalBars(options.get(0).underlyingSymbol, 30, ChronoUnit.DAYS);
			float closePrice = getAskPrice(options.get(0).underlyingSymbol);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject obj = arr.getJSONObject(options.get(i + count).symbol);
				float askPrice = obj.getFloat("ap");
				float bidPrice = obj.getFloat("bp");
				Date lastQuote = Date.from(Instant.parse(obj.getString("t")));
				options.get(i + count).updateFromQuote(askPrice, bidPrice, lastQuote, closePrice, bars);
			}
			count += Math.min(100, options.size() - count);
		}
		return options;

	}

	public ArrayList<String> getTopGainers(int amount) {
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("top", Integer.toString(amount));
		JSONObject response = (JSONObject) APIRequest("v1beta1/screener/stocks/movers", args, "data", "GET");
		JSONArray actives = response.getJSONArray("gainers");
		for (int i = 0; i < actives.length(); i++) {
			symbols.add(actives.getJSONObject(i).getString("symbol"));
		}
		return symbols;
	}

	public ArrayList<String> getTopLosers(int amount) {
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("top", Integer.toString(amount));
		JSONObject response = (JSONObject) APIRequest("v1beta1/screener/stocks/movers", args, "data", "GET");
		JSONArray actives = response.getJSONArray("losers");
		for (int i = 0; i < actives.length(); i++) {
			symbols.add(actives.getJSONObject(i).getString("symbol"));
		}
		return symbols;
	}

	public Bar getDailyBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/snapshots", args, "data", "GET");
		return new Bar(response.getJSONObject(symbol).getJSONObject("dailyBar"), 0, 24, symbol);
	}

	public Bar getPrevDailyBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/snapshots", args, "data", "GET");
		return new Bar(response.getJSONObject(symbol).getJSONObject("prevDailyBar"), 0, 24, symbol);
	}

	public Bar getMinuteBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/snapshots", args, "data", "GET");
		return new Bar(response.getJSONObject(symbol).getJSONObject("minuteBar"), 1, 0, symbol);
	}

	public Bars getHistoricalBars(String symbol, int amount, ChronoUnit unit) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		switch (unit) {
		case DAYS:
			args.put("timeframe", "1D");
			break;
		case HOURS:
			args.put("timeframe", "1H");
			break;
		case MINUTES:
			args.put("timeframe", "1T");
			break;
		default:
			args.put("timeframe", "1D");
			break;
		}
		args.put("limit", Integer.toString(amount));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = formatter.format(Date.from(Instant.now().minus(amount, unit)));
		args.put("start", startDate);
		args.put("adjustment", "split");
		JSONObject response = (JSONObject) APIRequest("v2/stocks/bars", args, "data", "GET");
		JSONArray arr = response.getJSONObject("bars").getJSONArray(symbol);
		JSONObject barObject = new JSONObject();
		barObject.put(symbol, arr);
		return new Bars(barObject);
	}

	public Market createMarketFromTickers(ArrayList<String> tickers, int days) {
		ArrayList<Bars> data = new ArrayList<Bars>();
		for (String ticker : tickers) {
			Bars bars = getHistoricalBars(ticker, days, ChronoUnit.DAYS);
			data.add(bars);
		}
		return new Market(data);
	}

	public HashMap<String, Bars> getMultipleHistoricalBars(List<String> symbols, int amount, ChronoUnit unit) {
		HashMap<String, Bars> barsHashMap = new HashMap<String, Bars>();
		String s = symbols.get(0);
		for (String symbol : symbols) {
			s += "," + symbol;
		}
		String nextPageToken = "";
		while (nextPageToken != null) {
			HashMap<String, String> args = new HashMap<String, String>();
			args.put("symbols", s);
			args.put("limit", "10000");
			switch (unit) {
			case DAYS:
				args.put("timeframe", "1D");
				break;
			case HOURS:
				args.put("timeframe", "1H");
				break;
			case MINUTES:
				args.put("timeframe", "1T");
				break;
			default:
				args.put("timeframe", "1D");
				break;
			}

			args.put("limit", Integer.toString(amount));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String startDate = formatter.format(Date.from(Instant.now().minus(amount, unit)));
			args.put("start", startDate);
			if (!nextPageToken.equals("")) {
				args.put("page_token", nextPageToken);
			}
			JSONObject response = (JSONObject) APIRequest("v2/stocks/bars", args, "data", "GET");
			try {
				nextPageToken = response.getString("next_page_token");
			} catch (Exception e) {
				nextPageToken = null;
			}

			JSONObject barsArray = response.getJSONObject("bars");
			Iterator<String> keysIterator = barsArray.keys();
			while (keysIterator.hasNext()) {
				String symbolString = keysIterator.next().toString();
				JSONArray arr = response.getJSONObject("bars").getJSONArray(symbolString);
				boolean hashMapContains = barsHashMap.containsKey(symbolString);
				Bars bars = new Bars();
				for (int i = 0; i < arr.length(); i++) {
					if (hashMapContains) {
						barsHashMap.get(symbolString).add(new Bar(arr.getJSONObject(i), 0, 24, symbolString));
					} else {
						bars.add(new Bar(arr.getJSONObject(i), 0, 24, symbolString));
					}
				}
				if (!hashMapContains) {
					barsHashMap.put(symbolString, bars);
				}
			}
		}
		return barsHashMap;
	}

	public Account getAccount() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = (JSONObject) APIRequest("v2/account", args, "api", "GET");
		return new Account(response);
	}

	public Object APIRequest(String path, Map<String, String> args, String api, String method) {
		String prefix = "https://";
		if (paper == true && api == "api") {
			api = "paper-api";
		}
		prefix += api;
		prefix += ".alpaca.markets/";
		path = prefix + path;
		// TODO add rate limit headers
		BodyPublisher body = HttpRequest.BodyPublishers.noBody();
		if (method.equals("POST")) {
			String dataString = "{";
			for (Map.Entry<String, String> entry : args.entrySet()) {
				dataString += "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\",";
			}
			dataString = dataString.substring(0, dataString.length() - 1);
			dataString += "}";
			body = HttpRequest.BodyPublishers.ofString(dataString);
		} else {
			String pathString = "";
			int s = args.size();
			if (s != 0) {
				pathString = "?";
			}
			for (Map.Entry<String, String> entry : args.entrySet()) {
				pathString += entry.getKey() + "=" + entry.getValue();
				s--;
				if (s != 0) {
					pathString += "&";
				}
			}
			path += pathString;
		}
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(path)).header("accept", "application/json")
				.header("content-type", "application/json").header("APCA-API-KEY-ID", public_key)
				.header("APCA-API-SECRET-KEY", private_key).method(method, body).build();

		HttpResponse<String> response;

		try {
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() > 300) {
				System.err.println("Response failed with error " + Integer.toString(response.statusCode()));
				System.err.println(response.toString());
				System.err.println(response.body());
			}
			try {
				return new JSONObject(response.body());
			} catch (org.json.JSONException e) {
				return new JSONArray(response.body());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
