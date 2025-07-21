package dailyTrader;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		DecisionEngine decisionEngine = new DecisionEngine(apiManager);
		Scheduler scheduler = new Scheduler(apiManager, decisionEngine);

		OptionChain chain = apiManager.getOptionsInRange("NVDA", 180, 180, 7);
		chain.updateAll();
		chain = chain.filterByOpenInterest(1000);
		//chain = chain.filterByAskPrice(account.buying_power);
		for (Option option : chain.options) {
			decisionEngine.addDecision(option);
		}
		scheduler.start();
	}
}
