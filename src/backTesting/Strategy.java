package backTesting;

import java.util.ArrayList;
import java.util.List;

import dailyTrader.Bars;


public class Strategy {
	public Strategy() {
		
	}
	
	public TradingAction decide(List<Bars> data, ArrayList<TradingAction> possibleActions) {
		return possibleActions.get(0);
	}
}
