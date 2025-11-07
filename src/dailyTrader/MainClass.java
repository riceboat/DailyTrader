package dailyTrader;

import serverHosting.Server;

public class MainClass {
	public static void main(String args[]) {
		String public_key = args[0];
		String private_key = args[1];
		APIManager apiManager = new APIManager(public_key, private_key, true);
		Server server = new Server(apiManager);
		server.startServer();
	}
}
