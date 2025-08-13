package dailyTrader;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		DecisionEngine decisionEngine = new DecisionEngine(apiManager);
		OptionChain chain = apiManager.getOptionsInRange("NVDA", 180, 220, 5);
		Account account = apiManager.getAccount();
		chain.updateAll();
		decisionEngine.addDecisions(chain);
		decisionEngine.sortOptions();
		System.out.println(account);
		System.out.println(decisionEngine.bestCallOption("NVDA", account.cash));
		System.out.println(decisionEngine.bestPutOption("NVDA", account.cash));
	}
}
