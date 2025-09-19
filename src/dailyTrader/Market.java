package dailyTrader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Market {
	private HashMap<String, Bars> symbolBars;
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

	public Bars getSymbolBars(String symbol) {
		return symbolBars.get(symbol);
	}

	public Market firstNDays(int days) {
		ArrayList<Bars> newData = new ArrayList<Bars>();
		for (Entry<String, Bars> entry : symbolBars.entrySet()) {
			Bars newBars = new Bars();
			for (int i = 0; i < days; i++) {
				newBars.add(entry.getValue().get(i));
			}
			newData.add(newBars);
		}
		return new Market(newData);
	}

	public void saveData(String path) {
		try (FileWriter csvWriter = new FileWriter(path)) {
			for (Entry<String, Bars> entry : symbolBars.entrySet()) {
				csvWriter.append(entry.getKey());
			}
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
