package dailyTrader;

public class MainClass {
	public static void main(String args[]) {
		String public_key = "PKJJGUZRUAD4BDPRN55F";
		String private_key = "v9IZBcphqOhv7T4JHZEksvzhnWBrLZkzlsRNXrgK";
		APIManager apiManager = new APIManager(public_key, private_key, true);
		Account account = apiManager.getAccount();
		DecisionEngine decisionEngine = new DecisionEngine(apiManager);
		// Scheduler scheduler = new Scheduler(apiManager, decisionEngine);

		OptionChain chain = apiManager.getOptionsInRange("NVDA", 160, 190, 7);
		chain = chain.filterByOpenInterest(1000);
		chain = chain.filterByAskPrice(account.buying_power);
		chain.updateAll();
		for (Option option : chain.options) {
			decisionEngine.addDecision(option);
		}
		// scheduler.start();
	}
}
