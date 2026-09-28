package strategies;

import java.util.ArrayList;

import backTesting.TradingAction;
import dailyTrader.Bars;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Position;
import dailyTrader.Side;

public class SellWorstPerformingEveryNDays extends Strategy {
	private int day;

	public SellWorstPerformingEveryNDays(int daysInbetween) {
		setParameterValue("daysInbetween", daysInbetween);
		day = 0;
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions) {
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		int daysInbetween = (int) getParameterValue("daysInbetween");
		if (portfolio.getCash() > 0 && day == 0) {
			for (TradingAction action : possibleActions) {
				if (action.getSide() == Side.LONG) {
					action.setPercentage(1.0);
					action.setJustificationString("We are buying everything to sell later");
					chosenActions.add(action);
				}
			}
		} else if (day > 0 && day % daysInbetween == 0) {
			Position worstPosition = null;
			double worstPNL = 0;
			for (Position position : portfolio.positions) {
				if (position.pnl < worstPNL) {
					worstPNL = position.pnl;
					worstPosition = position;
				}
			}
			ArrayList<Bars> symbolBars = market.getBars();
			String bestSymbol = null;
			double bestPNLinNDays = 0;
			for (Bars bars : symbolBars) {
				double pnl = bars.getNLastDays(daysInbetween).getTotalProfit();
				if (pnl > bestPNLinNDays) {
					bestSymbol = bars.symbol;
					bestPNLinNDays = pnl;
				}
			}
			if (worstPosition != null) {
				for (TradingAction action : possibleActions) {
					if (action.getSide() == Side.SELL && action.getSymbol().equals(worstPosition.symbol)) {
						action.setJustificationString(
								"This position performed the worst (" + worstPNL + "%) and ran out of time to improve");
						chosenActions.add(action);
					}
				}
			}
			if (bestSymbol != null) {
				for (TradingAction action : possibleActions) {
					if (action.getSide() == Side.LONG && action.getSymbol().equals(bestSymbol)) {
						action.setJustificationString(
								"This position performed the best (" + bestPNLinNDays + "%) and was bought");
						chosenActions.add(action);

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
