package dailyTrader;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import backTesting.TradingAction;

public class Portfolio implements JSONConvertible {
	public ArrayList<Position> positions;
	public Bars portfolioHistoryBars;
	private Account linkedAccount;
	private double cash;

	public Portfolio(JSONObject portfolioJSON, Account account) {
		positions = new ArrayList<Position>();
		this.linkedAccount = account;
		cash = account.getCash();
		JSONArray positionsJsonArray = portfolioJSON.getJSONArray("positions");
		for (int i = 0; i < positionsJsonArray.length(); i++) {
			Position position = new Position(positionsJsonArray.getJSONObject(i));
			positions.add(position);
		}
		this.portfolioHistoryBars = new Bars(portfolioJSON.getJSONObject("history"));
	}

	public void performTradingActions(ArrayList<TradingAction> tradingActions, Market market, Date date) {
		for (Position position : positions) {
			position.update(market.getSymbolValueOnDate(position.symbol, date));
		}
		double totalCommitment = 0;
		for (TradingAction tradingAction : tradingActions) {
			totalCommitment += tradingAction.getPercentage();
		}
		for (TradingAction tradingAction : tradingActions) {
			switch (tradingAction.getSide()) {
			case LONG: {
				double entryPrice = market.getSymbolValueOnDate(tradingAction.getSymbol(), date);
				double cashCommitment = cash * (tradingAction.getPercentage() / totalCommitment);
				double qtyBought = cashCommitment / entryPrice;
				Position newPosition = new Position(tradingAction.getSymbol(), tradingAction.getSide(), qtyBought,
						entryPrice, date);
				positions.add(newPosition);
				cash -= cashCommitment;
				totalCommitment -= tradingAction.getPercentage();
				break;
			}
			case SHORT: {
				// TODO implement shorting
				break;
			}
			case SELL: {
				double exitPrice = market.getSymbolValueOnDate(tradingAction.getSymbol(), date);
				Position soldPosition = getPositionByCode(tradingAction.getSymbol());
				double positionQty = soldPosition.qty;
				double qtySold = tradingAction.getPercentage() * positionQty;
				double cashGained = qtySold * exitPrice;
				cash += cashGained;
				positions.remove(soldPosition);
				break;
			}
			}

		}

	}

	public Bars getHistory() {
		return portfolioHistoryBars;
	}

	public Position getPositionByCode(String code) {
		for (Position position : positions) {
			if (position.symbol.equals(code)) {
				return position;
			}
		}
		return null;
	}

	public double getValue() {
		double value = getCash();
		for (Position position : positions) {
			value += position.qty * position.entryPrice + position.pnl;
		}
		return value;
	}

	public double getSimValue() {
		double value = cash;
		for (Position position : positions) {
			value += position.qty * position.entryPrice + position.pnl;
		}
		return value;
	}

	@Override
	public String toString() {
		String s = "";
		s += "Portfolio cash: " + Double.toString(getCash()) + "\n";
		s += "Portfolio sim cash: " + Double.toString(cash) + "\n";
		double value = getValue();
		double pnl = 0;
		for (Position position : positions) {
			pnl += position.pnl;
		}
		s += "Portfolio value: " + Double.toString(value) + "\n";
		s += "Portfolio sim value: " + Double.toString(getSimValue()) + "\n";
		s += "Portfolio PNL: " + Double.toString(pnl) + "\n\n";
		for (Position position : positions) {
			s += position.toString();
		}
		return s;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject portfolioJsonObject = getHistory().toJSON();
		JSONArray positionJsonArray = new JSONArray();
		for (Position position : positions) {
			positionJsonArray.put(position.toJSON());
		}
		portfolioJsonObject.put("positions", positionJsonArray);
		portfolioJsonObject.put("account", linkedAccount.toJSON());
		return portfolioJsonObject;
	}

	public double getCash() {
		return cash;
	}

}
