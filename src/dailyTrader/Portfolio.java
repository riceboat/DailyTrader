package dailyTrader;

import java.util.ArrayList;

public class Portfolio {
	ArrayList <Position> positions;
	public Portfolio() {
		positions = new ArrayList<Position>();
	}
	public void addPosition(Position position) {
		positions.add(position);
	}
	public String toString() {
		String s = ""; 
		for (Position position : positions) {
			s += position.toString();
		}
		return s;
	}
}
