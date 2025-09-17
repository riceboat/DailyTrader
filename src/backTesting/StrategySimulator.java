package backTesting;

import java.util.ArrayList;

import dailyTrader.Bar;
import dailyTrader.Bars;
import dailyTrader.Market;
import dailyTrader.Portfolio;
import dailyTrader.Position;
import dailyTrader.Side;
import dailyTrader.Type;

public class StrategySimulator {
	Strategy strategy;
	Portfolio portfolio;
	Market market;
	int day;
	int maxDays;

	public StrategySimulator(Strategy strategy, Market market, Portfolio portfolio) {
		this.strategy = strategy;
		this.portfolio = portfolio;
		this.market = market;
		this.maxDays = market.getDays();
		this.day = maxDays / 2;
	}

	public ArrayList<TradingAction> getPossibleActions() {
		ArrayList<TradingAction> possibleActions = new ArrayList<TradingAction>();
		for (Bars bars : market.getBars()) {
			Bar barToday = bars.get(bars.size() - 1);
			if (barToday.c < portfolio.cash) {
				possibleActions.add(new TradingAction(Type.STOCK, Side.LONG, 1, barToday.symbol));
				if (portfolio.cash > 2000) {
					possibleActions.add(new TradingAction(Type.STOCK, Side.SHORT, 1, barToday.symbol));
				}
			}
		}

		for (Position position : portfolio.positions) {
			possibleActions.add(new TradingAction(Type.STOCK, Side.SELL, 1, position.symbol));
			possibleActions.add(new TradingAction(Type.STOCK, Side.HOLD, 1, position.symbol));
		}

		return possibleActions;
	}

	public void performTradingAction(TradingAction tradingAction) {
		String symbol = tradingAction.codeString;
		Side side = tradingAction.side;
		double percent = tradingAction.percent;
		double currentPrice = getAssetValue(symbol);
		double qty = percent * (portfolio.cash / currentPrice);
		switch (tradingAction.side) {
		case LONG: {
			Position newPosition = new Position(symbol, side, qty, currentPrice);
			double cost = portfolio.cash * percent;
			portfolio.cash -= cost;
			portfolio.addPosition(newPosition);
		}
			break;
		case SHORT: {
			Position newPosition = new Position(symbol, side, qty, currentPrice);
			double cost = portfolio.cash * percent;
			portfolio.cash -= cost;
			portfolio.addPosition(newPosition);
		}
			break;
		case HOLD: {

		}
			break;
		case SELL: {
			Position position = portfolio.getPositionByCode(symbol);
			double cost = percent * getAssetValue(symbol) * position.qty;
			portfolio.cash += cost;
			portfolio.removePosition(position);
		}
			break;
		default:
			break;
		}
	}

	public double getAssetValue(String symbol) {
		for (Bars bars : market.getBars()) {
			if (symbol.equals(bars.symbol)) {
				return bars.get(day).c;
			}
		}
		System.err.println("????????????????");
		return 0.0;
	}

	public void updatePortfolio() {
		for (Position position : portfolio.positions) {
			position.update(getAssetValue(position.symbol));
		}
	}

	public void step() {
		ArrayList<TradingAction> possibleActions = getPossibleActions();
		TradingAction bestTradingAction = strategy.decide(market.getBars(), portfolio, possibleActions, day);
		System.out.println("Start of day " + Integer.toString(day) + ":\n");
		updatePortfolio();
		System.out.println(portfolio);
		System.out.println("POSSIBLE ACTIONS");
		System.out.println(possibleActions);
		System.out.println("SELECTED -> " + bestTradingAction);
		performTradingAction(bestTradingAction);
		day++;
	}

	public void run() {
		while (day < maxDays - 2) {
			step();
		}
	}
}
