package dailyTrader;

import org.json.JSONObject;

public class Position {
	String symbol;
	float qty;
	float pnl;
	float pnlpc;
	String side;
	public Position (JSONObject obj){
		this.symbol = obj.getString("symbol");
		this.qty = obj.getFloat("qty");
		this.pnlpc = obj.getFloat("unrealized_plpc");
		this.pnl = obj.getFloat("unrealized_pl");
		this.side = obj.getString("side");
	}
	
	public String toString() {
		String s = "Symbol: " + this.symbol + "\n";
		s += "Quantity: " + this.qty + "\n";
		s += "P&L: " + this.pnl + "\n";
		s += "P&L %: " + this.pnlpc + "\n";
		s += "Side: " + this.side + "\n";
		return s;
	}
}
