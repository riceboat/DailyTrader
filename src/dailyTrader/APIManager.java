package dailyTrader;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import org.json.*;

public class APIManager {
	String public_key;
	String private_key;

	public APIManager(String public_key, String private_key) {
		this.public_key = public_key;
		this.private_key = private_key;
	}

	public float getAskPrice(String symbol) {
		float price = 0.0f;
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("symbols", symbol);
		String request = requestBuilder("stocks/quotes/latest", args);
		String response = APIRequest(request);
		JSONObject obj = new JSONObject(response);
		price = (float) obj.getJSONObject("quotes").getJSONObject(symbol).get("ap");
		return price;
	}

//	public abstract float getBidPrice();
//
//	public abstract float buyStock();

	public String requestBuilder(String path, Map<String, String> args) {
		String prefix = "https://data.alpaca.markets/v2/";
		String result = prefix + path;
		int s = args.size();
		if (s != 0) {
			result += "?";
			s--;
			for (Map.Entry<String, String> entry: args.entrySet()) {
				result += entry.getKey() + "=" + entry.getValue();
			}
			if (s != 0) {
				result +="&";
			}
		}
		return result;
	}

	public String APIRequest(String requestString) {
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
			return response.body();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
