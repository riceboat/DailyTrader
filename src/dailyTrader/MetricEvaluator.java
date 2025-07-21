package dailyTrader;

public class MetricEvaluator {
	public MetricEvaluator() {

	}

	double normalDistribution(double x, double std, double mean) {
		return (Math.pow(Math.E, -0.5 * Math.pow((x - mean) / std, 2)) / (std * Math.sqrt(2 * Math.PI)));
	}

	double evaluate(Option option) {
		double probabilityOfProfit = option.getProbabilityOfProfit(option.closePrice);
		double probaibilityOfMaxLoss = option.getProbabilityOfMaxLoss(option.closePrice);
		double val = probabilityOfProfit - probaibilityOfMaxLoss;
		return val;
	}

//	double evaluate(MultiLeg multileg) {
//		double probabilityOfProfit = getProbabilityOfProfit(option.closePrice, option);
//		double probaibilityOfMaxLoss = getProbabilityOfMaxLoss(option.closePrice, option);
//		double val = probabilityOfProfit - probaibilityOfMaxLoss;
//		return val;
//	}
}
