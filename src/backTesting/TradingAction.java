package backTesting;

import dailyTrader.Side;
import dailyTrader.Type;

public class TradingAction {
	Side side;
	double percent;
	Type type;
	String codeString;

	public TradingAction(Type type, Side side, double percent, String codeString) {
		this.type = type;
		this.side = side;
		this.percent = percent;
		this.codeString = codeString;
	}

	public String toString() {
		String s = "";
		if (side == Side.LONG) {
			s += "LONG ";
		}
		else if (side == Side.SHORT) {
			s += "SHORT ";
		}
		else if (side == Side.SELL) {
			s += "SELL ";
		}
		else if (side == Side.HOLD) {
			s += "HOLD ";
		}
		s += codeString + " ";
		if (type == Type.OPTION) {
			s += "OPTION ";
		}
		else if (type == Type.STOCK) {
			s += "STOCK ";
		}
		s+="\n";
		return s;
	}
}
