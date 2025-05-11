package dailyTrader;

public class MainClass {
	public static void main(String args[]) {
		String public_key = "PKUM7ULODBBRTDICG49P";
		String private_key = "0JQPuCrrdhQOcTrTqnB8p7sSYVp2mtxYXqPSNUez";
		APIManager apiManager = new APIManager(public_key, private_key, true);
		DecisionEngine decisionEngine = new DecisionEngine();
		Scheduler scheduler = new Scheduler(apiManager, decisionEngine);
		scheduler.start();
	}
}
