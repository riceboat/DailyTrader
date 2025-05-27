package dailyTrader;

import java.util.ArrayList;

public class DecisionEngine {
	APIManager apiManager;
	float lastPrice = 0;
	public DecisionEngine(APIManager apiManager) {
		this.apiManager = apiManager;
		lastPrice = apiManager.getAskPrice("NVDA");
	}
	public void frame() {
		float newPrice = apiManager.getAskPrice("NVDA");
		Account account = apiManager.getAccount();
		float cash = account.cash;
		float maxBuyAmount = (cash / newPrice);
		ArrayList<Position> posList = apiManager.getPositions();
		float maxSellAmount = 0;
		if (posList.size() != 0) {
			Position p = posList.get(0);
			p = apiManager.getPositions().get(0);
			maxSellAmount = p.qty;
		}
		if (newPrice > lastPrice && posList.size() != 0) {
			System.out.println("sell");
			apiManager.createOrder("NVDA", maxSellAmount * 0.99f, "sell");
		}
		else if (newPrice < lastPrice) {
			System.out.println("buy");
			apiManager.createOrder("NVDA", maxBuyAmount * 0.99f, "buy");
		}
			
		lastPrice = newPrice;
	}
}
