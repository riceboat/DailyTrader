package dailyTrader;

import AlgoTrader.APIManager;

public class MainClass {
	public static void main(String args[]) {
		String public_key = "PKUM7ULODBBRTDICG49P";
		String private_key = "0JQPuCrrdhQOcTrTqnB8p7sSYVp2mtxYXqPSNUez";
		APIManager apiManager = new APIManager(public_key, private_key);
		apiManager.getAskPrice("AAPL");
	}
}
