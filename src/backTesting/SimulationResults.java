package backTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.JSONObject;

import dailyTrader.Bars;
import dailyTrader.JSONConvertible;

public class SimulationResults implements JSONConvertible {
	private Bars bars;
	private HashMap<Integer, ArrayList<TradingAction>> actions;

	public SimulationResults(Bars bars, HashMap<Integer, ArrayList<TradingAction>> actions) {
		this.bars = bars;
		this.actions = actions;
	}

	public HashMap<Integer, ArrayList<TradingAction>> getActions() {
		return actions;
	}

	public Bars getBars() {
		return bars;
	}

	@Override
	public JSONObject toJSON() {
		JSONObject jsonObject = bars.toJSON();
		JSONObject actionsJsonObject = new JSONObject();
		for (Entry<Integer, ArrayList<TradingAction>> entry : actions.entrySet()) {
			actionsJsonObject.put(entry.getKey().toString(), entry.getValue());
		}
		jsonObject.put("actions", actionsJsonObject);
		return jsonObject;
	}
}
