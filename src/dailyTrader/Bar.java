package dailyTrader;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.json.JSONObject;

public class Bar {
	public double o;
	public double c;
	public double h;
	public double l;
	public Date end;
	public Date start;
	public String symbol;

	public Bar(JSONObject obj, int minutes, int hours, String symbol) {
		this.o = obj.getFloat("o");
		this.c = obj.getFloat("c");
		this.h = obj.getFloat("h");
		this.l = obj.getFloat("l");
		this.symbol = symbol;
		this.end = Date.from(Instant.parse(obj.getString("t")));
		Instant before = end.toInstant().minus(Duration.ofHours(hours));
		before = before.minus(Duration.ofMinutes(minutes));
		this.start = Date.from(before);
	}

	public Bar(String symbol, double c, Date start, Date end) {
		this.c = c;
		this.start = start;
		this.end = end;
		this.symbol = symbol;
	}

	public String toString() {
		String s = "Open: " + Double.toString(o) + "\n";
		s += "Close: " + Double.toString(c) + "\n";
		s += "High: " + Double.toString(l) + "\n";
		s += "Low: " + Double.toString(h) + "\n";
		s += "From: " + start.toString() + "\n";
		s += "To: " + end.toString() + "\n";
		return s;
	}

	public JSONObject toJSON() {
		JSONObject jsonBar = new JSONObject();
		jsonBar.put("o", o);
		jsonBar.put("c", c);
		jsonBar.put("h", h);
		jsonBar.put("l", l);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String isoDate = dateFormatter.format(end);
		jsonBar.put("t", isoDate);
		return jsonBar;
	}
}
