package dailyTrader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class Pair {
	String x;
	String y;

	public Pair(String x, String y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object obj) {
		return obj.hashCode() == hashCode();
	}

	public int hashCode() {
		return x.hashCode() + y.hashCode();
	}

	public Boolean has(String s) {
		return s.equals(x) || s.equals(y);

	}

	public String toString() {
		return x + " - " + y;
	}
}

public class CorrelationMatrix {
	HashMap<Pair, Double> matrix;

	public CorrelationMatrix() {
		matrix = new HashMap<Pair, Double>();
	}

	public CorrelationMatrix(HashMap<Pair, Double> matrix) {
		this.matrix = matrix;

	}

	public String toString() {
		String s = "";
		for (Entry<Pair, Double> entry : matrix.entrySet()) {
			s += entry.getKey() + " = " + entry.getValue() + "\n";
		}
		return s;
	}

	public CorrelationMatrix correlationBetweenAll(ArrayList<Bars> barsList) {
		ArrayList<Pair> ignoreList = new ArrayList<Pair>();
		matrix = new HashMap<Pair, Double>();
		for (Bars bars : barsList) {
			for (Bars bars2 : barsList) {
				Pair pair = new Pair(bars.symbol, bars2.symbol);
				if (!ignoreList.contains(pair) && !bars.symbol.equals(bars2.symbol)) {
					matrix.put(pair, bars.crossCorrelationAtLag(bars2, 0));
					ignoreList.add(pair);
				}
			}
		}
		return new CorrelationMatrix(matrix);
	}


	public CorrelationMatrix correlationsAbove(double val) {
		HashMap<Pair, Double> correlations = new HashMap<Pair, Double>();
		for (Entry<Pair, Double> entry : matrix.entrySet()) {
			if (entry.getValue() > val) {
				correlations.put(entry.getKey(), entry.getValue());
			}
		}
		return new CorrelationMatrix(correlations);
	}

	public CorrelationMatrix correlationsBelow(double val) {
		HashMap<Pair, Double> correlations = new HashMap<Pair, Double>();
		for (Entry<Pair, Double> entry : matrix.entrySet()) {
			if (entry.getValue() < val) {
				correlations.put(entry.getKey(), entry.getValue());
			}
		}
		return new CorrelationMatrix(correlations);
	}

	public CorrelationMatrix correlationWithSymbol(ArrayList<Bars> barsList, Bars bars2) {
		ArrayList<Pair> ignoreList = new ArrayList<Pair>();
		matrix = new HashMap<Pair, Double>();
		for (Bars bars : barsList) {
				Pair pair = new Pair(bars.symbol, bars2.symbol);
				if (!ignoreList.contains(pair) && !bars.symbol.equals(bars2.symbol)) {
					System.out.println(bars.symbol);
					matrix.put(pair, bars.crossCorrelationAtLag(bars2, 0));
					ignoreList.add(pair);
				}
			}
		return new CorrelationMatrix(matrix);
	}

}
