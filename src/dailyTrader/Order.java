package dailyTrader;

import java.time.Instant;
import java.util.Date;

import org.json.JSONObject;

public class Order {
	String symbol;
	float qty;
	Date time_created;
	Date time_filled;
	Date time_expired;
	boolean filled;
	boolean expired;
	String side;
	String id;

	public Order(JSONObject obj) {
		this.symbol = obj.getString("symbol");
		this.id = obj.getString("id");
		this.qty = Float.parseFloat(obj.getString("qty"));
		this.side = obj.getString("side");
		this.time_created = Date.from(Instant.parse(obj.getString("created_at")));

		this.filled = obj.getString("status").equals("filled");
		this.expired = obj.getString("status").equals("expired");

		if (filled) {
			this.time_filled = Date.from(Instant.parse(obj.getString("filled_at")));
		}
		if (expired) {
			this.time_expired = Date.from(Instant.parse(obj.getString("expired_at")));
		}
	}

	public String toString() {
		String s = "Symbol: " + symbol + "\n";
		s += "Side: " + side + "\n";
		s += "Quantity: " + Float.toString(qty) + "\n";
		s += "Created At: " + time_created.toString() + "\n";
		if (filled) {
			s += "Filled At: " + time_filled.toString() + "\n";
		}
		if (expired) {
			s += "Expired At: " + time_expired.toString() + "\n";
		}
		s += "Order ID: " + id + "\n";
		s += "\n";
		return s;
	}
}
