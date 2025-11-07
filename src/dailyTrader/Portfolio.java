package dailyTrader;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class Portfolio implements JSONConvertible {
	public ArrayList<Position> positions;
	public Bars portfolioHistoryBars;
	private Account linkedAccount;
	private double simCash;

	public Portfolio(JSONObject portfolioJSON, Account account) {
		positions = new ArrayList<Position>();
		this.linkedAccount = account;
		simCash = 0;
		JSONArray positionsJsonArray = portfolioJSON.getJSONArray("positions");
		for (int i = 0; i < positionsJsonArray.length(); i++) {
			Position position = new Position(positionsJsonArray.getJSONObject(i));
			addPosition(position);
		}
		this.portfolioHistoryBars = new Bars(portfolioJSON.getJSONObject("history"));
	}

	public void addPosition(Position position) {
		positions.add(position);
	}

	public Bars getHistory() {
		return portfolioHistoryBars;
	}

	public void removePosition(Position position) {
		Position posToRemove = null;
		for (Position pos : positions) {
			if (pos.symbol.equals(position.symbol)) {
				posToRemove = pos;
			}
		}
		positions.remove(posToRemove);
	}

	public Position getPositionByCode(String code) {
		for (Position position : positions) {
			if (position.symbol.equals(code)) {
				return position;
			}
		}
		return null;
	}

	public double getValue() {
		double value = getCash();
		for (Position position : positions) {
			value += position.qty * position.entryPrice + position.pnl;
		}
		return value;
	}

	public String toString() {
		String s = "";
		s += "Portfolio cash: " + Double.toString(getCash()) + "\n";
		double value = getValue();
		double pnl = 0;
		for (Position position : positions) {
			pnl += position.pnl;
		}
		s += "Portfolio value: " + Double.toString(value) + "\n";
		s += "Portfolio PNL: " + Double.toString(pnl) + "\n\n";
		for (Position position : positions) {
			s += position.toString();
		}
		return s;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject portfolioJsonObject = getHistory().toJSON();
		JSONArray positionJsonArray = new JSONArray();
		for (Position position : positions) {
			positionJsonArray.put(position.toJSON());
		}
		portfolioJsonObject.put("positions", positionJsonArray);
		portfolioJsonObject.put("account", linkedAccount.toJSON());
		return portfolioJsonObject;
	}

	public double getCash() {
		if (simCash == 0) {
			return linkedAccount.getCash();
		}
		else {
			return simCash;
		}
	}

	public void setCash(double cash) {
		this.simCash = cash;
	}
}
