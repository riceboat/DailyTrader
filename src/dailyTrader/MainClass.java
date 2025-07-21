package dailyTrader;

public class MainClass {
	public static void main(String args[]) {
		String public_key = "PKAYQ8H1Z5S2VYPIFKI3";
		String private_key = "lFYhjCZ53kZeFrXDZWDBKJd4z48XOA2XrnDEyz2T";
		APIManager apiManager = new APIManager(public_key, private_key, true);

		DecisionEngine decisionEngine = new DecisionEngine(apiManager);
		// Scheduler scheduler = new Scheduler(apiManager, decisionEngine);

		OptionChain chain = apiManager.getOptionsInRange("NVDA", 160, 190, 7);
		chain.updateAll();
		for (Option option : chain.options) {
			decisionEngine.addDecision(option);
		}
		// scheduler.start();
	}
}
