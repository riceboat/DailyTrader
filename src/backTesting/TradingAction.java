package backTesting;

enum Side {
	LONG, SHORT, HOLD
}

enum Type {
	STOCK, OPTION
}

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
			s += "BUY ";
		}
		else if (side == Side.SHORT) {
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
		return s;
	}
}
