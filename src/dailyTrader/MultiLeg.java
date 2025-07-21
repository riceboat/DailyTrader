package dailyTrader;

import java.util.ArrayList;

public class MultiLeg {
	ArrayList<Option> options;
	public MultiLeg(ArrayList<Option> options) {
		this.options = options;
	}
	
	public void add(Option o) {
		options.add(o);
	}
}
