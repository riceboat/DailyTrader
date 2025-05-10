package dailyTrader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
		JSONObject response = APIRequest(requestBuilder("v2/stocks/quotes/latest", args, "data"));
		float price = Float.parseFloat(response.getJSONObject("quotes").getJSONObject(symbol).get("ap").toString());
		return price;
	}

	public float getBidPrice(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = APIRequest(requestBuilder("v2/stocks/quotes/latest", args, "data"));
		float price = Float.parseFloat(response.getJSONObject("quotes").getJSONObject(symbol).get("bp").toString());
		return price;
	}
	
	public Date getMarketTime() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = APIRequest(requestBuilder("v2/clock", args, "api"));
		return stringToLocalDate(response.get("timestamp").toString());
	}
	
	public Date getNextMarketOpen() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = APIRequest(requestBuilder("v2/clock", args, "api"));
		return stringToLocalDate(response.get("next_open").toString());
	}
	public Date getNextMarketClose() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = APIRequest(requestBuilder("v2/clock", args, "api"));
		return stringToLocalDate(response.get("next_close").toString());
	}
	
	public boolean isMarketOpen() {
		HashMap<String, String> args = new HashMap<String, String>();
		JSONObject response = APIRequest(requestBuilder("v2/clock", args, "api"));
		return response.get("is_open").toString() == "true";
	}
	public Date stringToLocalDate(String s) {
		return Date.from(Instant.parse(s));
		
	}
	
	public ArrayList<String> getMostActiveSymbols(int amount){
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("by","volume");
		args.put("top",Integer.toString(amount));
		JSONObject response = APIRequest(requestBuilder("v1beta1/screener/stocks/most-actives", args, "data"));
		JSONArray actives = response.getJSONArray("most_actives");
		for (int i = 0; i < actives.length(); i++) {
			symbols.add(actives.getJSONObject(i).getString("symbol"));
		}
		return symbols;
	}
	
	public ArrayList<Order> getOpenOrders() {
		ArrayList<Order> orders = new ArrayList<Order>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("status","open");
		JSONArray response = APIRequest(requestBuilder("v2/orders", args, "api")).getJSONArray(null);
		for (int i = 0; i < response.length(); i++) {
			orders.add(new Order(response.getJSONObject(i)));
		}
		return orders;
	}
	
	public ArrayList<String> getTopGainers(int amount){
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("top",Integer.toString(amount));
		JSONObject response = APIRequest(requestBuilder("v1beta1/screener/stocks/movers", args, "data"));
		JSONArray actives = response.getJSONArray("gainers");
		for (int i = 0; i < actives.length(); i++) {
			symbols.add(actives.getJSONObject(i).getString("symbol"));
		}
		return symbols;
	}
	
	public ArrayList<String> getTopLosers(int amount){
		ArrayList<String> symbols = new ArrayList<String>();
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("top",Integer.toString(amount));
		JSONObject response = APIRequest(requestBuilder("v1beta1/screener/stocks/movers", args, "data"));
		JSONArray actives = response.getJSONArray("losers");
		for (int i = 0; i < actives.length(); i++) {
			symbols.add(actives.getJSONObject(i).getString("symbol"));
		}
		return symbols;
	}
	
	public Bar getDailyBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = APIRequest(requestBuilder("v2/stocks/snapshots", args, "data"));
		return new Bar(response.getJSONObject(symbol).getJSONObject("dailyBar"), 0, 24);
	}
	
	public Bar getPrevDailyBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = APIRequest(requestBuilder("v2/stocks/snapshots", args, "data"));
		return new Bar(response.getJSONObject(symbol).getJSONObject("prevDailyBar"), 0 , 24);
	}
	
	public Bar getMinuteBar(String symbol) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		JSONObject response = APIRequest(requestBuilder("v2/stocks/snapshots", args, "data"));
		return new Bar(response.getJSONObject(symbol).getJSONObject("minuteBar"), 1, 0);
	}
	public String requestBuilder(String path, Map<String, String> args, String api) {
		String prefix = "https://";
		if (paper == true && api == "api") {
			api = "paper-api";
		}
		prefix += api; 
		prefix += ".alpaca.markets/";
		String result = prefix + path;
		int s = args.size();
		if (s != 0) {
			result += "?";
			for (Map.Entry<String, String> entry: args.entrySet()) {
				s--;
				result += entry.getKey() + "=" + entry.getValue();
				if (s != 0) {
					result +="&";
				}
			}
		}
		return result;
	}

	public JSONObject APIRequest(String requestString) {
		//TODO add rate limit headers
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(requestString))
				.header("accept", "application/json")
			    .header("APCA-API-KEY-ID", public_key)
			    .header("APCA-API-SECRET-KEY", private_key)
			    .method("GET", HttpRequest.BodyPublishers.noBody())
			    .build();
		HttpResponse<String> response;
		try {
			response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				System.err.println("Response failed with error " + Integer.toString(response.statusCode()));
				System.err.println(response.toString());
				System.err.println(response.body());
			}
			try {
				return new JSONObject(response.body());
			}
			catch (org.json.JSONException e){
				return new JSONObject(response.body());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
