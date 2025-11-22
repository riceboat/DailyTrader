package strategies;

import java.util.ArrayList;

import backTesting.TradingAction;
import dailyTrader.Bars;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Side;

// this strategy shorts the lowest macd and longs the highest
public class MACDLongShort extends Strategy {

	public MACDLongShort(int longMA, int shortMA, int signalMA) {
		setParameterValue("longMA", longMA);
		setParameterValue("shortMA", shortMA);
		setParameterValue("signalMA", signalMA);
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio, ArrayList<TradingAction> possibleActions,
			int day) {
		double lowestDiff = Double.POSITIVE_INFINITY;
		double highestDiff = Double.NEGATIVE_INFINITY;
		String lowestDiffSymbol = null;
		String highestDiffSymbol = null;
		int longMA = (int) getParameterValue("longMA");
		int shortMA = (int) getParameterValue("shortMA");
		int signalMA = (int) getParameterValue("signalMA");
		for (Bars bars : market.getBars()) {
			double macd = bars.getSMALastNDays(shortMA) - bars.getSMALastNDays(longMA);
			double signal = bars.getSMALastNDays(signalMA);
			double diff = macd - signal;
			if (diff > highestDiff) {
				highestDiff = diff;
				highestDiffSymbol = bars.symbol;
			}
			if (diff < lowestDiff) {
				lowestDiff = diff;
				lowestDiffSymbol = bars.symbol;
			}
		}
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		for (TradingAction action : possibleActions) {
			Bars actionBars = market.getSymbolBars(action.getSymbol());

			if (action.getSide() == Side.LONG && action.getSymbol().equals(lowestDiffSymbol)) {
				chosenActions.add(action);
			} else if (action.getSide() == Side.SHORT && action.getSymbol().equals(highestDiffSymbol)) {
				chosenActions.add(action);
			} else if (action.getSide() == Side.SELL) {
				double macd = actionBars.getSMALastNDays(shortMA) - actionBars.getSMALastNDays(longMA);
				double signal = actionBars.getSMALastNDays(signalMA);
				double actionDiff = macd - signal;
				// System.out.println(actionDiff);
				if (actionDiff > 0) {
					chosenActions.add(action);
				}
			}
		}
		return chosenActions;
	}
}
