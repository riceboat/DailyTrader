package strategies;

import java.util.ArrayList;

import backTesting.TradingAction;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Position;
import dailyTrader.Side;

public class SellAtNPercentProfit extends Strategy {
	private int day;

	public SellAtNPercentProfit(double profitPercentage) {
		setParameterValue("profitPercentage", profitPercentage);
		day = 0;
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions) {
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		int profitPercentage = (int) getParameterValue("profitPercentage");

		if (portfolio.getCash() > 0 && day == 0) {
			for (TradingAction action : possibleActions) {
				if (action.getSide() == Side.LONG) {
					action.setPercentage(1.0);
					action.setJustificationString("We are buying everything to sell later");
					chosenActions.add(action);
				}
			}
		} else if (day > 0) {
			for (Position position : portfolio.positions) {
				if (position.pnlpc > profitPercentage) {
					for (TradingAction action : possibleActions) {
						if (action.getSide() == Side.SELL && action.getSymbol().equals(position.symbol)) {
							action.setJustificationString("We sold this stock because it made more than "
									+ profitPercentage + "% profit (" + position.pnlpc + ")");
							chosenActions.add(action);
						}
					}
				}
			}

		}
		day++;
		return chosenActions;
	}

	@Override
	public int getDataCollectionPeriod() {
		return 0;
	}

}
