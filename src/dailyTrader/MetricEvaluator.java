package dailyTrader;

public class MetricEvaluator {
	public MetricEvaluator() {

	}

	double evaluate(Option option) {
		double probabilityOfProfit = option.getProbabilityOfProfit();

		System.out.println(option);
		return probabilityOfProfit;
	}
}
