package dailyTrader;

import java.util.ArrayList;

public class Portfolio {
	ArrayList<Position> positions;

	public Portfolio() {
		positions = new ArrayList<Position>();
	}

	public void addPosition(Position position) {
		positions.add(position);
	}

	public Position getPositionByCode(String code) {
		for (Position position : positions) {
			if (position.symbol.equals(code)) {
				return position;
			}
		}
		return null;
	}
	public String toString() {
		String s = "";
		for (Position position : positions) {
			s += position.toString();
		}
		return s;
	}
}
