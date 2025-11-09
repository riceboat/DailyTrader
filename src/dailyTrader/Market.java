package dailyTrader;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

public class Market implements JSONConvertible {
	private HashMap<String, Bars> symbolBars;
	private int days;

	public Market(ArrayList<Bars> data) {
		symbolBars = new HashMap<String, Bars>();
		days = data.get(0).size();
		for (Bars bars : data) {
			if (days > bars.size()) {
				System.out.println(bars.symbol + " has only " + bars.size() + " data points!, removing");
			} else {
				symbolBars.put(bars.symbol, bars);
			}
		}
	}

	public Market(JSONObject marketJSON) {
		ArrayList<Bars> data = new ArrayList<Bars>();
		JSONArray jsonArray = marketJSON.getJSONArray("market");
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject barsObject = jsonArray.getJSONObject(i);
			data.add(new Bars(barsObject));
		}
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

	public JSONObject toJSON() {
		JSONObject jsonMarket = new JSONObject();
		for (Entry<String, Bars> entry : symbolBars.entrySet()) {
			Bars bars = entry.getValue();
			jsonMarket.put(bars.symbol, bars.toJSON());
		}
		return jsonMarket;
	}
}
