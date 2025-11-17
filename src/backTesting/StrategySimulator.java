package backTesting;

import java.util.ArrayList;
import java.util.HashMap;

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
	int day;
	int maxDays;
	boolean debug;

	public StrategySimulator(Strategy strategy, Market market, Portfolio portfolio, boolean debug) {
		this.strategy = strategy;
		this.portfolio = portfolio;
		this.portfolio.setSimCash(portfolio.getCash());
		this.market = market;
		this.maxDays = market.getDays();
		this.day = maxDays / 2;
		this.debug = debug;
	}

	public ArrayList<TradingAction> getPossibleActions() {
		ArrayList<TradingAction> possibleActions = new ArrayList<TradingAction>();
		for (Bars bars : market.getBars()) {
			Bar barToday = bars.get(bars.size() - 1);
			if (barToday.c < portfolio.getSimCash()) {
				possibleActions.add(new TradingAction(Type.STOCK, Side.LONG, 1, barToday.symbol));
				if (portfolio.getSimCash() > 2000) {
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

	public void reset() {
		day = maxDays / 2;
	}

	public void performTradingActions(ArrayList<TradingAction> tradingActions) {
		double totPercent = 0;
		for (TradingAction tradingAction : tradingActions) {
			totPercent += tradingAction.percent;
		}
		for (TradingAction tradingAction : tradingActions) {
			String symbol = tradingAction.codeString;
			Side side = tradingAction.side;
			double percent = tradingAction.percent;
			double currentPrice = getAssetValue(symbol);
			switch (tradingAction.side) {
			case LONG: {
				double cashCommitment = ((percent / totPercent) * portfolio.getSimCash());
				totPercent -= percent;
				double qty = cashCommitment / currentPrice;
				Position newPosition = new Position(symbol, side, qty, currentPrice);
				System.out.println(portfolio.getSimCash());
				portfolio.setSimCash(portfolio.getSimCash() - cashCommitment);
				portfolio.addPosition(newPosition);
			}
				break;
			case SHORT: {
				double cashCommitment = ((percent / totPercent) * portfolio.getSimCash());
				totPercent -= percent;
				double qty = cashCommitment / currentPrice;
				Position newPosition = new Position(symbol, side, qty, currentPrice);
				portfolio.setSimCash(portfolio.getSimCash() - cashCommitment);
				portfolio.addPosition(newPosition);
			}
				break;
			case HOLD: {

			}
				break;
			case SELL: {
				Position position = portfolio.getPositionByCode(symbol);
				double cost = percent * getAssetValue(symbol) * position.qty;
				portfolio.setSimCash(portfolio.getSimCash() + cost);
				portfolio.removePosition(position);
			}
				break;
			default:
				break;
			}
		}
	}

	public double getAssetValue(String symbol) {
		for (Bars bars : market.getBars()) {
			if (symbol.equals(bars.symbol)) {
				return bars.get(day).c;
			}
		}
		return 0.0;
	}

	public void updatePortfolio() {
		for (Position position : portfolio.positions) {
			position.update(getAssetValue(position.symbol));
		}
	}

	public ArrayList<TradingAction> step() {
		ArrayList<TradingAction> possibleActions = getPossibleActions();
		ArrayList<TradingAction> bestActions = strategy.decide(market.firstNDays(day), portfolio, possibleActions, day - maxDays / 2);
		debugPrint("Start of day " + Integer.toString(day - maxDays / 2) + ":\n");
		updatePortfolio();
		debugPrint(portfolio);
		debugPrint("POSSIBLE ACTIONS");
		debugPrint(possibleActions);
		debugPrint("SELECTED -> " + bestActions);
		performTradingActions(bestActions);
		debugPrint(portfolio);
		debugPrint("End of day\n");
		day++;
		return bestActions;
	}

	public void debugPrint(Object s) {
		if (debug) {
			System.out.println(s.toString());
		}
	}

	public SimulationResults run() {
		Bars portfolioBars = new Bars();
		String strategyName = strategy.getName();
		HashMap<Integer, ArrayList<TradingAction>> actionsHashMap = new HashMap<Integer, ArrayList<TradingAction>>();
		while (day < maxDays - 2) {
			Bar oldBar = market.getBars().get(0).get(day);
			Bar newBar = new Bar(strategyName, portfolio.getSimValue(), oldBar.start, oldBar.end);
			portfolioBars.add(newBar);
			ArrayList<TradingAction> selectedActions = step();
			if (selectedActions.size() != 0) {
				actionsHashMap.put(day - maxDays / 2, selectedActions);
			}
		}
		return new SimulationResults(portfolioBars, actionsHashMap);
	}
}
