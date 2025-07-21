package dailyTrader;

import java.util.ArrayList;

public class Bars {
	ArrayList<Bar> bars;
	
	public Bars() {
		bars = new ArrayList<Bar>();
	}

	public void add(Bar bar) {
		bars.add(bar);
	}

	public double getAverage() {
		double tot = 0;
		for (Bar bar : bars) {
			tot += bar.c;
		}
		return tot / bars.size();
	}

	public double getAverageLogReturns() {
		double tot = 0;
		for (int i = 1; i < bars.size(); i++) {
			double logReturn = Math.log(bars.get(i).c / bars.get(i - 1).c);
			tot += logReturn;
		}
		return tot / bars.size();
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
}
