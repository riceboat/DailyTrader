package dailyTrader;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bars implements JSONConvertible {
	ArrayList<Bar> bars;
	public String symbol;

	public Bars() {
		bars = new ArrayList<Bar>();
	}

	public Bars(JSONObject barsJsonObject) {
		Iterator<String> iter = barsJsonObject.keys();
		symbol = iter.next();
		JSONArray barsArray = barsJsonObject.getJSONArray(symbol.toString());
		bars = new ArrayList<Bar>();
		for (int i = 0; i < barsArray.length(); i++) {
			Bar bar = new Bar(barsArray.getJSONObject(i), 0, 24, symbol);
			this.add(bar);
		}
	}

	public void add(Bar bar) {
		bars.add(bar);
		symbol = bar.symbol;
	}

	public double getAverage() {
		double tot = 0;
		for (Bar bar : bars) {
			tot += bar.c;
		}
		return tot / bars.size();
	}

	public Bar get(int i) {
		return bars.get(i);
	}

	public int size() {
		return bars.size();
	}

	public double getAverageLogReturns() {
		double tot = 0;
		for (int i = 1; i < bars.size(); i++) {
			double logReturn = Math.log(bars.get(i).c / bars.get(i - 1).c);
			tot += logReturn;
		}
		return tot / bars.size();
	}

	public double getReturnsOnDay(int i) {
		return get(i).c - get(i).o;
	}

	public double getAverageReturn() {
		double tot = 0;
		for (int i = 1; i < bars.size(); i++) {
			double diff = bars.get(i).c - bars.get(i).o;
			tot += diff;
		}
		return tot / bars.size();
	}

	public double getStdReturn() {
		double tot = 0;
		double average = getAverageReturn();
		for (int i = 1; i < bars.size(); i++) {
			double diff = bars.get(i).c - bars.get(i).o;
			tot += Math.pow(diff - average, 2);
		}
		return Math.sqrt(tot / bars.size());
	}

	public double getStandardDeviation() {
		double tot = 0;
		double average = getAverage();
		for (int i = 1; i < bars.size(); i++) {
			tot += Math.pow(bars.get(i).c - average, 2);
		}
		return Math.sqrt(tot / bars.size());
	}

	public double getVolatility() {
		double tot = 0;
		double averageLogReturn = getAverageLogReturns();
		for (int i = 1; i < bars.size(); i++) {
			double logReturn = Math.log(bars.get(i).c / bars.get(i - 1).c);
			tot += Math.pow(logReturn - averageLogReturn, 2);
		}
		double vol = Math.sqrt(tot / bars.size());
		vol = vol * Math.sqrt(252);
		return vol;
	}

	public Bars getNLastDays(int days) {
		Bars newBars = new Bars();
		int exclude = bars.size() - days;
		for (int i = exclude; i < bars.size(); i++) {
			newBars.add(bars.get(i));
		}
		return newBars;
	}

	public Bars getNFirstDays(int days) {
		Bars newBars = new Bars();
		for (int i = 0; i < days; i++) {
			newBars.add(bars.get(i));
		}
		return newBars;
	}

	public double getSMALastNDays(int days) {
		return getNLastDays(days).getAverage();
	}

	public ArrayList<Double> getValues() {
		ArrayList<Double> values = new ArrayList<Double>();
		for (Bar bar : bars) {
			values.add(bar.c);
		}
		return values;
	}

	public double crossCorrelationAtLag(Bars bars2, int lag) {
		double meanA = this.getAverageReturn();
		double meanB = bars2.getAverageReturn();
		double stdA = this.getStdReturn();
		double stdB = bars2.getStdReturn();
		double sum = 0;
		for (int i = 0; i < this.size(); i++) {
			double normA = (this.getReturnsOnDay(i) - meanA) / stdA;
			double normB = (bars2.getReturnsOnDay(i) - meanB) / stdB;
			sum += normA * normB;
		}
		double result = sum / this.size();
		return Math.round(result * 100.0) / 100.0;
	}

	public JSONObject toJSON() {
		JSONObject jsonBars = new JSONObject();
		JSONArray jsonBarArray = new JSONArray();
		for (Bar bar : bars) {
			JSONObject jsonBar = bar.toJSON();
			jsonBarArray.put(jsonBar);
		}
		jsonBars.put(symbol, jsonBarArray);
		return jsonBars;
	}
}
