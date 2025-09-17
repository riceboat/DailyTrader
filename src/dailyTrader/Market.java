package dailyTrader;

import java.util.ArrayList;
import java.util.HashMap;

public class Market {
	private HashMap <String, Bars> symbolBars;
	private int days;
	public Market(ArrayList<Bars> data) {
		symbolBars = new HashMap<String, Bars>();
		days = data.get(0).size();
		for (Bars bars : data) {
			symbolBars.put(bars.symbol, bars);
		}
	}
	public int getDays() {
		return days;
	}
	public ArrayList<Bars> getBars() {
		return new ArrayList<Bars>(symbolBars.values());
	}

}
