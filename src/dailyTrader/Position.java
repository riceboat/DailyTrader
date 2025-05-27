package dailyTrader;

import org.json.JSONObject;

public class Position {
	String symbol;
	float qty;
	public Position (JSONObject obj){
		this.symbol = obj.getString("symbol");
		this.qty = obj.getFloat("qty");
	}
	
	public String toString() {
		String s = "Symbol";
		
		return s;
	}
}
