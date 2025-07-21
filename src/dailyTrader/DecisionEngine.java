package dailyTrader;

import java.util.HashMap;
import java.util.Map.Entry;

public class DecisionEngine {
	APIManager apiManager;
	float lastPrice = 0;
	HashMap<Option, Double> decisions;
	public DecisionEngine(APIManager apiManager) {
		this.apiManager = apiManager;
		lastPrice = apiManager.getAskPrice("NVDA");
		decisions  = new HashMap<Option, Double>();
	}
	public void frame() {
		chooseDecision();
	}
	public void addDecision(Option option) {
		MetricEvaluator eval = new MetricEvaluator();
		double score = eval.evaluate(option);
		decisions.put(option, score);
	}
	public Option chooseDecision() {
		double maxValue = Double.MIN_VALUE;
		Option bestDecision = null;
		for (Entry<Option, Double> decision : decisions.entrySet()) {
			if (decision.getValue() > maxValue) {
				maxValue = decision.getValue();
				bestDecision = decision.getKey();
			}
		}
		return bestDecision;
	}
}
