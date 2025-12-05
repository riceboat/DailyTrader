package dailyTrader;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

public class Position {
	public String symbol;
	public double qty;
	public double pnl;
	public Side side;
	public Type type;
	public double entryPrice;
	public double pnlpc;
	private Date startDate;

	public Position(JSONObject obj) {
		this.symbol = obj.getString("symbol");
		this.qty = obj.getFloat("qty");
		this.pnl = obj.getFloat("unrealized_pl");
		String sideString = obj.getString("side");
		if (sideString.equals("long")) {
			this.side = Side.LONG;
		} else if (sideString.equals("short")) {
			this.side = Side.SHORT;
		}
	}

	public Position() {
	}

	public Position(String symbol, Side side, double qty, double entryPrice, Date startDate) {
		this.symbol = symbol;
		this.side = side;
		this.entryPrice = entryPrice;
		this.qty = qty;
		this.startDate = startDate;
	}

	public void update(double currentPrice) {
		if (side == Side.LONG) {
			this.pnl = currentPrice * qty - entryPrice * qty;
			this.pnlpc = ((currentPrice * qty) / (entryPrice * qty) - 1) * 100;
		} else if (side == Side.SHORT) {
			this.pnl = entryPrice * qty - currentPrice * qty;
			this.pnlpc = ((entryPrice * qty) / (currentPrice * qty) - 1) * 100;
		}

	}

	@Override
	public String toString() {
		String s = "Symbol: " + this.symbol + "\n";
		s += "Quantity: " + this.qty + "\n";
		s += "P&L: " + this.pnl + "\n";
		s += "P&L  %: " + this.pnlpc + "\n";
		s += "Side: " + this.side + "\n";
		s += "Entry Price: " + Double.toString(entryPrice) + "\n\n";
		return s;
	}

	public JSONObject toJSON() {
		JSONObject positionJsonObject = new JSONObject();
		positionJsonObject.put("symbol", symbol);
		positionJsonObject.put("pnl", pnl);
		positionJsonObject.put("pnlpc", pnlpc);
		positionJsonObject.put("side", side);
		positionJsonObject.put("qty", qty);
		positionJsonObject.put("entry_price", entryPrice);
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		String isoDate = dateFormatter.format(startDate);
		positionJsonObject.put("start_date", isoDate);
		return positionJsonObject;
	}

}
