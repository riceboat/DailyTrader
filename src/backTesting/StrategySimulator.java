package backTesting;

import java.util.ArrayList;
import java.util.Date;

import dailyTrader.Bar;
import dailyTrader.Bars;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Position;
import dailyTrader.Side;
import dailyTrader.Type;
import strategies.Strategy;

public class StrategySimulator {
	Strategy strategy;
	Portfolio portfolio;
	Market market;
	boolean debug;

	public StrategySimulator(Strategy strategy, Market market, Portfolio portfolio, boolean debug) {
		this.strategy = strategy;
		this.portfolio = portfolio;
		this.market = market;
		this.debug = debug;
	}

	public ArrayList<TradingAction> getPossibleActions() {
		ArrayList<TradingAction> possibleActions = new ArrayList<TradingAction>();
		for (Bars bars : market.getBars()) {
			Bar barToday = bars.get(bars.size() - 1);
			if (barToday.c < portfolio.getCash()) {
				possibleActions.add(new TradingAction(Type.STOCK, Side.LONG, 1, barToday.symbol));
				if (portfolio.getCash() > 2000) {
					possibleActions.add(new TradingAction(Type.STOCK, Side.SHORT, 1, barToday.symbol));
				}
			}
		}

		for (Position position : portfolio.positions) {
			possibleActions.add(new TradingAction(Type.STOCK, Side.SELL, 1, position.symbol));
		}

		return possibleActions;
	}

	public ArrayList<TradingAction> step(Date currentDate) {
		ArrayList<TradingAction> possibleActions = getPossibleActions();
		ArrayList<TradingAction> bestActions = strategy.decide(market.dataBefore(currentDate), portfolio,
				possibleActions);
		debugPrint("Start of day " + currentDate.toString() + ":\n");
		debugPrint("SELECTED -> " + bestActions);
		portfolio.performTradingActions(bestActions, market, currentDate);
		debugPrint(portfolio);
		return bestActions;
	}

	public void debugPrint(Object s) {
		if (debug) {
			System.out.println(s.toString());
		}
	}

	public Bars run() {
		Bars portfolioBars = new Bars();
		String strategyName = strategy.getName();
		int dataCollectionDays = strategy.getDataCollectionPeriod();
		int dataPointsCollected = 0;
		for (Date currentDate : market.getOpenDates()) {
			if (dataPointsCollected < dataCollectionDays) {
				dataPointsCollected++; // Dont run unless we have collected enough data for the given strategy
			} else {
				ArrayList<TradingAction> selectedActions = step(currentDate);
				Bar newBar = new Bar(strategyName, portfolio.getSimValue(), currentDate, currentDate);
				for (TradingAction tradingAction : selectedActions) {
					newBar.AddAction(tradingAction);
				}
				portfolioBars.add(newBar);
			}
		}
		return portfolioBars;
	}
}
