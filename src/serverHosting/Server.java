package serverHosting;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dailyTrader.APIManager;

public class Server {
	private HttpServer server;
	static String indexPageURI;
	APIManager apiManager;
	ArrayList<Thread> threads;
	public Server(APIManager apiManager) {
		threads = new ArrayList<Thread>();
		this.apiManager = apiManager;
		indexPageURI = "/";
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
			server.createContext(indexPageURI, new MyHandler());
			server.setExecutor(null); // creates a default executor
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startServer() {
		server.start();
	}

	public void stopServer() {
		server.stop(0);
	}

	
	class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			ServerEventHandler eventHandler =  new ServerEventHandler(apiManager, httpExchange);
			Thread t1 = new Thread(eventHandler);
			t1.start();
		}
	}

}
