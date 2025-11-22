package strategies;

import java.util.ArrayList;

import backTesting.TradingAction;
import dailyTrader.Bars;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Side;

public class SMACrossover extends Strategy {

	public SMACrossover(int longMA, int shortMA) {
		setParameterValue("longMA", longMA);
		setParameterValue("shortMA", shortMA);
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio, ArrayList<TradingAction> possibleActions,
			int day) {
		ArrayList<TradingAction> chosenActions = new ArrayList<>();
		int longMA = (int) getParameterValue("longMA");
		int shortMA = (int) getParameterValue("shortMA");
		for (TradingAction action : possibleActions) {
			Bars actionBars = market.getSymbolBars(action.getSymbol());
			double longMAValue = actionBars.getSMALastNDays(longMA);
			double shortMAValue = actionBars.getSMALastNDays(shortMA);
			if (action.getSide() == Side.LONG && shortMAValue > longMAValue) {
				chosenActions.add(action);
			}
			if (action.getSide() == Side.SELL && shortMAValue < longMAValue) {
				chosenActions.add(action);
			}
			if (action.getSide() == Side.SHORT && shortMAValue < longMAValue) {
				chosenActions.add(action);
			}
		}
		return chosenActions;
	}

	@Override
	public int getDataCollectionPeriod() {
		return (int) Math.max(getParameterValue("longMA"), getParameterValue("shortMA"));
	}

}
