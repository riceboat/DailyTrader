package dailyTrader;

public class MetricEvaluator {
	public MetricEvaluator() {

	}

	double evaluate(Option option) {
		double probabilityOfProfit = option.getProbabilityOfProfit(option.closePrice);
		double probaibilityOfMaxLoss = option.getProbabilityOfLoss(option.closePrice);
		double val = probabilityOfProfit - probaibilityOfMaxLoss;
		return val;
	}
	double getPairsTradingScore() {
		
		return 0.0;
	}
}
