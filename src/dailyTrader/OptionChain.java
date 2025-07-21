package dailyTrader;

import java.util.ArrayList;

public class OptionChain {
	ArrayList<Option> options;
	APIManager apiManager;

	public OptionChain(ArrayList<Option> options, APIManager apiManager) {
		this.options = options;
		this.apiManager = apiManager;
	}

	void updateAll() {
		options = apiManager.getOptionQuotes(options);
	}

	public String toString() {
		String s = "";
		for (Option o : options) {
			s += o.toString();
		}
		return s;
	}

	Option getOptionByCode(String code) {
		for (Option option : options) {
			if (option.symbol.equals(code)) {
				return option;
			}
		}
		return null;
	}
	OptionChain filterByOpenInterest(int oi) {
		ArrayList<Option> newChain = new ArrayList<Option>();
		for (Option o : options) {
			if (o.openInterest > oi) {
				newChain.add(o);
			}
		}
		return new OptionChain(newChain, apiManager);
	}
	
	OptionChain filterByAskPrice(double buying_power) {
		ArrayList<Option> newChain = new ArrayList<Option>();
		for (Option o : options) {
			if (o.askPrice * 100 < buying_power) {
				newChain.add(o);
			}
		}
		return new OptionChain(newChain, apiManager);
	}
	
	OptionChain filterByBidPrice(int price) {
		ArrayList<Option> newChain = new ArrayList<Option>();
		for (Option o : options) {
			if (o.bidPrice * 100 < price) {
				newChain.add(o);
			}
		}
		return new OptionChain(newChain, apiManager);
	}
	
	OptionChain filterByImpliedVolatility(double iv) {
		ArrayList<Option> newChain = new ArrayList<Option>();
		for (Option o : options) {
			if (o.iv > iv) {
				newChain.add(o);
			}
		}
		return new OptionChain(newChain, apiManager);
	}
}
