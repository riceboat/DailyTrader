package dailyTrader;

import org.json.JSONObject;

public class Account {
	String id;
	int daytrade_count;
	double cash;
	double buying_power;
	public Account (JSONObject obj){
		this.id = obj.getString("id");
		this.cash = Double.parseDouble(obj.getString("cash"));
		this.buying_power = Double.parseDouble(obj.getString("buying_power"));
		this.daytrade_count = obj.getInt("daytrade_count");
		
	}
	
	public String toString() {
		String s = "Account ID: " + id + "\n";
		s += "Buying power: " + Double.toString(buying_power) + "\n";
		s += "Cash: " + Double.toString(cash) + "\n";
		s += "daytrade_count: " + Integer.toString(daytrade_count) + "\n";
		s += "\n";
		return s;
	}
}