package dailyTrader;

import java.util.ArrayList;

public class Portfolio {
	public ArrayList<Position> positions;
	public double cash;

	public Portfolio(double cash) {
		positions = new ArrayList<Position>();
		this.cash = cash;
	}

	public void addPosition(Position position) {
		positions.add(position);
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
		double value = cash;
		for (Position position : positions) {
			value += position.qty * position.entryPrice + position.pnl;
		}
		return value;
	}
	public String toString() {
		String s = "";
		s += "Portfolio cash: " + Double.toString(cash) + "\n";
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
}
