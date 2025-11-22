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
		Portfolio portfolio = apiManager.getPortfolio(30);
		System.out.println(portfolio);
		LinkedHashMap<String, OptionChain> chains = new LinkedHashMap<String, OptionChain>();
		String[] symbols = { "AAPL", // Apple Inc.
				"MSFT", // Microsoft Corp.
				"GOOGL", // Alphabet Inc. (Class A)
				"AMZN", // Amazon.com Inc.
				"TSLA", // Tesla Inc.
				"NVDA", // NVIDIA Corp.
				"META", // Meta Platforms Inc. (formerly Facebook)
				"JPM", // JPMorgan Chase & Co.
				"V", // Visa Inc. (Class A)
				"JNJ", // Johnson & Johnson
				"WMT", // Walmart Inc.
				"XOM", // Exxon Mobil Corp.
				"PG", // Procter & Gamble Co.
				"KO", // The Coca-Cola Co.
				"PEP", // PepsiCo Inc.
				"MCD", // McDonald's Corp.
				"NKE", // NIKE Inc. (Class B)
				"NFLX", // Netflix Inc.
				"INTC", // Intel Corp.
				"CSCO", // Cisco Systems Inc.
				"ORCL", // Oracle Corp.
				"AMD", // Advanced Micro Devices Inc.
				"AVGO", // Broadcom Inc.
				"TXN", // Texas Instruments Inc.
				"UNH", // UnitedHealth Group Inc.
				"LLY", // Eli Lilly and Co.
				"PFE", // Pfizer Inc.
		};

		double cash = apiManager.getAccount().cash;
		for (String symbol : symbols) {
			double price = apiManager.getAskPrice(symbol);
			int low = (int) (price * 0.75);
			int high = (int) (price * 1.25);

			OptionChain chain = apiManager.getOptionsInRange(symbol, low, high, 30);
			if (chain != null) {
				System.out.println(Integer.toString(chain.options.size()) + " Options from " + symbol);
				chain.updateAll();
				chain = chain.filterByOpenInterest(1000);
				chain = chain.filterByAskPrice(cash);
				chains.put(symbol, chain);
				System.out.println(chain);
				for (Option option : chain.options) {
					addDecision(option);
				}
			}
		}
		decisions = bestPerStock();
		decisions = sortOptions();
		System.out.println(decisions);
		// System.out.println(apiManager.getMostRecentNewsToday(symbols));
		ArrayList<Option> heldOptions = new ArrayList<Option>();
		for (Position position : portfolio.positions) {
			Option option = apiManager.getOptionByCode(position.symbol);
			apiManager.getOptionQuote(option);
			if (option != null) {
				heldOptions.add(option);
				addDecision(option);
			}
		}
		ArrayList<Option> sellTheseOptions = new ArrayList<Option>();
		for (Option option : heldOptions) {
			if (decisions.get(option) < 30 || portfolio.getPositionByCode(option.symbol).pnlpc > 0.03) {
				sellTheseOptions.add(option);
			}
		}
		ArrayList<Option> buyTheseOptions = new ArrayList<Option>();
		Option bestOption = bestOption(cash);
		if (bestOption != null) {
			if (decisions.get(bestOption) > 40) {
				cash -= bestOption.askPrice * 100;
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

	public void addDecisions(OptionChain chain) {
		MetricEvaluator eval = new MetricEvaluator();
		ArrayList<Option> options = chain.options;
		for (Option option : options) {
			double score = eval.evaluate(option);
			decisions.put(option, score);
		}
	}

	public Option bestOption(double cash) {
		Option best = null;
		double top = Double.NEGATIVE_INFINITY;
		for (Option option : decisions.keySet()) {
			if (decisions.get(option) > top && option.askPrice * 100 < cash) {
				top = decisions.get(option);
				best = option;
			}
		}
		return best;
	}

	public Option bestCallOption(String underlying, double cash) {
		Option best = null;
		double top = Double.NEGATIVE_INFINITY;
		for (Option option : decisions.keySet()) {
			if (decisions.get(option) > top && option.askPrice * 100 < cash && option.type.equals("call")
					&& option.underlyingSymbol.equals(underlying)) {
				top = decisions.get(option);
				best = option;
			}
		}
		return best;
	}

	public Option bestPutOption(String underlying, double cash) {
		Option best = null;
		double top = Double.NEGATIVE_INFINITY;
		for (Option option : decisions.keySet()) {
			if (decisions.get(option) > top && option.askPrice * 100 < cash && option.type.equals("put")
					&& option.underlyingSymbol.equals(underlying)) {
				top = decisions.get(option);
				best = option;
			}
		}
		return best;
	}

	public LinkedHashMap<Option, Double> bestPerStock() {
		LinkedHashMap<Option, Double> topOptions = new LinkedHashMap<Option, Double>();
		ArrayList<String> symbolsSeen = new ArrayList<String>();
		for (Entry<Option, Double> e : decisions.entrySet()) {
			String symbol = e.getKey().underlyingSymbol;
			if (!symbolsSeen.contains(symbol)) {
				topOptions.put(e.getKey(), e.getValue());
				symbolsSeen.add(e.getKey().underlyingSymbol);
			}
		}
		return topOptions;
	}

	public LinkedHashMap<Option, Double> sortOptions() {
		LinkedHashMap<Option, Double> topOptions = decisions.entrySet().stream().sorted(Entry.comparingByValue())
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		return topOptions;
	}
}
