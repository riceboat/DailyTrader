package dailyTrader;

import java.time.Instant;
import java.util.Date;

import org.json.JSONObject;

public class Order {
	String symbol;
	float qty;
	Date time_created;
	Date time_filled;
	public Order (JSONObject obj) {
		String symbol = obj.getString("symbol");
		float qty = obj.getFloat("qty");
		this.time_created = Date.from(Instant.parse(obj.getString("created_at")));
	}
}
