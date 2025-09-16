package backTesting;

import java.util.ArrayList;

import dailyTrader.Bar;
import dailyTrader.Bars;
import dailyTrader.Portfolio;
import dailyTrader.Position;

public class StrategySimulator {
	Strategy strategy;
	double cash;
	Portfolio portfolio;
	ArrayList<Bars> data = new ArrayList<Bars>();
	int day;
	int maxDays;

	public StrategySimulator(Strategy strategy, ArrayList<Bars> data, Portfolio portfolio) {
		this.strategy = strategy;
		this.cash = portfolio.cash;
		this.portfolio = portfolio;
		this.data = data;
		this.maxDays = data.get(0).size();
		this.day = maxDays / 2;
	}

	public ArrayList<TradingAction> getPossibleActions() {
		ArrayList<TradingAction> possibleActions = new ArrayList<TradingAction>();
		for (Bars bars : data) {
			Bar barToday = bars.get(bars.size() - 1);
			if (barToday.c < cash) {
				possibleActions.add(new TradingAction(Type.STOCK, Side.LONG, 1, barToday.symbol));
			}
		}

		for (Position position : portfolio.positions) {
			possibleActions.add(new TradingAction(Type.STOCK, Side.SHORT, 1, position.symbol));
			possibleActions.add(new TradingAction(Type.STOCK, Side.HOLD, 1, position.symbol));
		}

		return possibleActions;
	}

	public void performTradingAction(TradingAction tradingAction) {
		switch (tradingAction.side) {
		case LONG: {
			Position newPosition = new Position();
			newPosition.symbol = tradingAction.codeString;
			double cost = cash;
			newPosition.qty = cash / getAssetValue(tradingAction.codeString);
			this.cash -= cost;
			portfolio.addPosition(newPosition);
		}
			break;
		case SHORT: {
			Position position = portfolio.getPositionByCode(tradingAction.codeString);
			double cost = tradingAction.percent * getAssetValue(tradingAction.codeString) * position.qty;
			position.qty -= position.qty * tradingAction.percent;
			this.cash += cost;
			if (position.qty == 0.0) {
				portfolio.removePosition(position);
			}
		}
			break;
		case HOLD: {

		}
			break;
		}
	}

	public double getAssetValue(String symbol) {
		for (Bars bars : data) {
			if (symbol.equals(bars.symbol)) {
				return bars.get(day).c;
			}
		}
		return 0.0;
	}

	public double getNetWorth() {
		double heldValue = 0.0;
		for (Position position : portfolio.positions) {
			double qty = position.qty;
			heldValue += qty * getAssetValue(position.symbol);
		}

		return cash + heldValue;
	}

	public void step() {
		ArrayList<TradingAction> possibleActions = getPossibleActions();
		TradingAction bestTradingAction = strategy.decide(data, possibleActions);
		performTradingAction(bestTradingAction);
		System.out.println(bestTradingAction);
		day++;
	}

	public void run() {
		double startWorth = getNetWorth();
		while (day < maxDays - 2) {
			step();
		}
		System.out.println(getNetWorth() - startWorth);
	}
}
