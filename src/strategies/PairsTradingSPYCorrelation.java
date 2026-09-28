package strategies;

import java.util.ArrayList;

import backTesting.TradingAction;
import dailyTrader.CorrelationMatrix;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Side;

public class PairsTradingSPYCorrelation extends Strategy {

	public PairsTradingSPYCorrelation(int longCorrelationWindow, int shortCorrelationWindow) {
		setParameterValue("longCorrelationWindow", longCorrelationWindow);
		setParameterValue("shortCorrelationWindow", shortCorrelationWindow);
	}

	@Override
	public ArrayList<TradingAction> decide(Market market, Portfolio portfolio,
			ArrayList<TradingAction> possibleActions) {
		int longCorrelationWindow = (int) getParameterValue("longCorrelationWindow");
		int shortCorrelationWindow = (int) getParameterValue("shortCorrelationWindow");
		ArrayList<TradingAction> chosenActions = new ArrayList<TradingAction>();
		CorrelationMatrix longCorrelationMatrix = new CorrelationMatrix();
		longCorrelationMatrix = longCorrelationMatrix.correlationWithSymbol(
				market.getLastNDays(longCorrelationWindow).getBars(),
				market.getSymbolBars("SPY").getNLastDays(longCorrelationWindow));
		longCorrelationMatrix = longCorrelationMatrix.correlationsAbove(0.9);
		CorrelationMatrix shortCorrelationMatrix = new CorrelationMatrix();
		shortCorrelationMatrix = shortCorrelationMatrix.correlationWithSymbol(
				market.getLastNDays(shortCorrelationWindow).getBars(),
				market.getSymbolBars("SPY").getNLastDays(shortCorrelationWindow));
		shortCorrelationMatrix = shortCorrelationMatrix.correlationsBelow(0);
		for (TradingAction action : possibleActions) {
			if (longCorrelationMatrix.hasSymbol(action.getSymbol())
					&& shortCorrelationMatrix.hasSymbol(action.getSymbol()) && action.getSide() == Side.LONG) {
				for (TradingAction otherAction : possibleActions) {
					if (otherAction.getSymbol().equals("SPY") && otherAction.getSide() == Side.SHORT) {
						chosenActions.add(otherAction);
					}
				}
			}
		}
		return chosenActions;
	}

	@Override
	public int getDataCollectionPeriod() {
		return Math.max((int) getParameterValue("longCorrelationWindow"),
				(int) getParameterValue("shortCorrelationWindow"));
	}

}
