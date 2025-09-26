package serverHosting;

import dailyTrader.APIManager;

public class ServerEventHandler {
	int i=0;
	APIManager apiManager;
	public ServerEventHandler(APIManager apiManager) {
		this.apiManager = apiManager;
	}

	public void call(String requestString) {
		System.out.println(requestString);
	}
}
