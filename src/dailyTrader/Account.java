package dailyTrader;

import org.json.JSONObject;

public class Account {
	String id;
	int daytrade_count;
	float cash;
	float buying_power;
	public Account (JSONObject obj){
		this.id = obj.getString("id");
		this.cash = Float.parseFloat(obj.getString("cash"));
		this.buying_power = Float.parseFloat(obj.getString("buying_power"));
		this.daytrade_count = obj.getInt("daytrade_count");
		
	}
	
	public String toString() {
		String s = "Account ID: " + id + "\n";
		s += "Buying power: " + Float.toString(buying_power) + "\n";
		s += "Cash: " + Float.toString(cash) + "\n";
		s += "daytrade_count: " + Integer.toString(daytrade_count) + "\n";
		s += "\n";
		return s;
	}
}