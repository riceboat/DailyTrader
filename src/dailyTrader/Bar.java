package dailyTrader;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.json.JSONObject;

public class Bar {
	float o;
	float c;
	float h;
	float l;
	Date end;
	Date start;
	public Bar(JSONObject obj, int minutes, int hours) {
		this.o = obj.getFloat("o");
		this.c = obj.getFloat("c");
		this.h = obj.getFloat("h");
		this.l = obj.getFloat("l");
		this.end = Date.from(Instant.parse(obj.getString("t")));
		Instant before = end.toInstant().minus(Duration.ofHours(hours));
		before = before.minus(Duration.ofMinutes(minutes));
		this.start = Date.from(before);
	}
	public String toString() {
		String s = "Open: " + Float.toString(o) + "\n";
		s += "Close: " + Float.toString(c) + "\n";
		s += "High: " + Float.toString(l) + "\n";
		s += "Low: " + Float.toString(h) + "\n";
		s += "From: " + start.toString() + "\n";
		s += "To: " + end.toString() + "\n";
		return s;
	}
}
