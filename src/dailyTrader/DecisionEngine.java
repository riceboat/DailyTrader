package dailyTrader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DecisionEngine {
	APIManager apiManager;
	float lastPrice = 0;
	LinkedHashMap<Option, Double> decisions;

	public DecisionEngine(APIManager apiManager) {
		this.apiManager = apiManager;
		lastPrice = apiManager.getAskPrice("NVDA");
		decisions = new LinkedHashMap<Option, Double>();
	}

	public void frame() {
		Portfolio portfolio = apiManager.getPortfolio();
		System.out.println(portfolio);
		OptionChain chain = apiManager.getOptionsInRange("NVDA", 150, 200, 7);
		chain.updateAll();
		for (Option option : chain.options) {
			addDecision(option);
		}
		MetricEvaluator evaluator = new MetricEvaluator();

		ArrayList<Option> heldOptions = new ArrayList<Option>();
		for (Position position : portfolio.positions) {
			Option option = chain.getOptionByCode(position.symbol);
			if (option != null) {
				heldOptions.add(option);
			}
		}
		ArrayList<Option> sellTheseOptions = new ArrayList<Option>();
		for (Option option : heldOptions) {
			if (evaluator.evaluate(option) < 30 || portfolio.getPositionByCode(option.symbol).pnlpc > 0.03) {
				sellTheseOptions.add(option);
			}
		}
		ArrayList<Option> buyTheseOptions = new ArrayList<Option>();
		double buyingPower = apiManager.getAccount().cash;
		Option bestOption = bestOption(buyingPower);
		if (bestOption != null) {
			if (evaluator.evaluate(bestOption) > 40) {
				buyingPower -= bestOption.askPrice * 100;
				buyTheseOptions.add(bestOption);
			}
		}
		for (Option option : sellTheseOptions) {
			System.out.println("Selling Option: " + option.toString());
			apiManager.createOrder(option.symbol, portfolio.getPositionByCode(option.symbol).qty, "sell");
		}
		for (Option option : buyTheseOptions) {
			System.out.println("Buying Option: " + option.toString());
			apiManager.createOrder(option.symbol, 1.0, "buy");
		}
	}

	public void addDecision(Option option) {
		MetricEvaluator eval = new MetricEvaluator();
		double score = eval.evaluate(option);
		decisions.put(option, score);
	}

	public Option bestOption(double cash) {
		Option best = null;
		double top = Double.MIN_VALUE;
		for (Option option : decisions.keySet()) {
			if (decisions.get(option) > top && option.askPrice * 100 < cash) {
				top = decisions.get(option);
				best = option;
			}
		}
		return best;
	}

	public LinkedHashMap<Option, Double> sortOptions() {
		LinkedHashMap<Option, Double> topOptions = decisions.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return topOptions;
	}
}
