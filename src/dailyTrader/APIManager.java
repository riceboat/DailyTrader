package dailyTrader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
		float price = Float.parseFloat(response.getJSONObject("quotes").getJSONObject(symbol).get("ap").toString());
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

	public ArrayList<Position> getPositions() {
		ArrayList<Position> positions = new ArrayList<Position>();
		HashMap<String, String> args = new HashMap<String, String>();
		JSONArray response = (JSONArray) APIRequest("v2/positions", args, "api", "GET");
		for (int i = 0; i < response.length(); i++) {
			positions.add(new Position(response.getJSONObject(i)));
		}
		return positions;
	}

	public void closeAllPositions() {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("cancel_orders", "true");
		APIRequest("v2/positions", args, "api", "DELETE");
	}

	public JSONObject createOrder(String symbol, float qty, String side) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbol", symbol);
		args.put("qty", Float.toString(qty));
		args.put("side", side);
		args.put("type", "market");
		args.put("time_in_force", "day");
		JSONObject response = (JSONObject) APIRequest("v2/orders", args, "api", "POST");
		return response;
	}
	
	public OptionChain getOptions(String symbol){
		ArrayList<Option> options = new ArrayList<Option>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("underlying_symbols", symbol);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(Date.from(Instant.now()));
		args.put("expiration_date_gte", dateString);
		JSONObject response = (JSONObject) APIRequest("v2/options/contracts", args, "api","GET");
		JSONArray array = response.getJSONArray("option_contracts");
		for (int i = 0; i < array.length(); i++) {
			options.add(new Option(array.getJSONObject(i)));
		}
		OptionChain chain = new OptionChain(options, this);
		return chain;
	}
	
	public OptionChain getOptionsInRange(String symbol, int low, int high){
		ArrayList<Option> options = new ArrayList<Option>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("underlying_symbols", symbol);
		args.put("strike_price_gte", Integer.toString(low));
		args.put("strike_price_lte", Integer.toString(high));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(Date.from(Instant.now()));
		args.put("expiration_date_gte", dateString);
		JSONObject response = (JSONObject) APIRequest("v2/options/contracts", args, "api","GET");
		JSONArray array = response.getJSONArray("option_contracts");
		for (int i = 0; i < array.length(); i++) {
			options.add(new Option(array.getJSONObject(i)));
		}
		OptionChain chain = new OptionChain(options, this);
		return chain;
	}
	
	public Option getOptionQuote(Option o) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", o.symbol);
		JSONObject response = (JSONObject) APIRequest("v1beta1/options/quotes/latest", args, "data","GET");
		JSONObject obj = response.getJSONObject("quotes").getJSONObject(o.symbol);
		
		float askPrice = obj.getFloat("ap");
		float bidPrice = obj.getFloat("bp");
		Date lastQuote = Date.from(Instant.parse(obj.getString("t")));
		float closePrice = getAskPrice(o.underlyingSymbol);
		o.updateFromQuote(askPrice, bidPrice, lastQuote, closePrice);
		return o;
		
	}
	public ArrayList<Option> getOptionQuotes(ArrayList<Option> options) {
		HashMap<String, String> args = new HashMap<String, String>();
		String s = "";
		for (int i = 0; i < options.size(); i++) {
			s += options.get(i).symbol;
			if (i != options.size() - 1) {
				s += "%2C";
			}
		}
		args.put("symbols", s);
		JSONObject response = (JSONObject) APIRequest("v1beta1/options/quotes/latest", args, "data","GET");
		JSONObject arr = response.getJSONObject("quotes");
		float closePrice = getAskPrice(options.get(0).underlyingSymbol);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = arr.getJSONObject(options.get(i).symbol);
			float askPrice = obj.getFloat("ap");
			float bidPrice = obj.getFloat("bp");
			Date lastQuote = Date.from(Instant.parse(obj.getString("t")));
			options.get(i).updateFromQuote(askPrice, bidPrice, lastQuote, closePrice);
		}
		
		return options;
		
	}
	public ArrayList<String> getTopGainers(int amount) {
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("top", Integer.toString(amount));
		JSONObject response = (JSONObject) APIRequest("v1beta1/screener/stocks/movers", args, "data","GET");
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
		return new Bar(response.getJSONObject(symbol).getJSONObject("dailyBar"), 0, 24);
	}

	public Bar getPrevDailyBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/snapshots", args, "data", "GET");
		return new Bar(response.getJSONObject(symbol).getJSONObject("prevDailyBar"), 0, 24);
	}

	public Bar getMinuteBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = (JSONObject) APIRequest("v2/stocks/snapshots", args, "data", "GET");
		return new Bar(response.getJSONObject(symbol).getJSONObject("minuteBar"), 1, 0);
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
		}
		else {
			String pathString = "";
			int s = args.size();
			if (s!=0) {
				pathString = "?";
			}
			for (Map.Entry<String, String> entry : args.entrySet()) {
				pathString +=  entry.getKey() + "=" + entry.getValue();
				s--;
				if (s!=0) {
					pathString+="&";
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
