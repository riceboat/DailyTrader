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
}
