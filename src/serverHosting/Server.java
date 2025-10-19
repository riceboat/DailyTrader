package serverHosting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	private HttpServer server;
	static String indexPageURI;
	ServerEventHandler eventHandler;

	public Server() {
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
		;
	}

	public void addEventHandler(ServerEventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	static String readFile(String filePath) {
		try {
			return Files.readString(Paths.get(filePath));
		} catch (IOException e) {
			return null;
		}
	}

	String responseHandler(String uriString, String requestString) {
		if (uriString.equals("")) {
			return readFile("pages/index.html");
		} else if (uriString.equals("api")) {
			System.out.println("APICALL");
			return eventHandler.call(requestString);
		} else {
			return readFile(uriString);
		}
	}

	class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			String uriString = t.getRequestURI().toString().substring(1);
			String response = null;
			InputStream inputStream = t.getRequestBody();
			Scanner s = new Scanner(inputStream).useDelimiter("\\A");
			String requestString = s.hasNext() ? s.next() : "";
			s.close();
			if (t.getRequestMethod().equals("GET")) {
				response = responseHandler(uriString, requestString);
				if (response == null) {
					response = readFile("pages/404.html");
					t.sendResponseHeaders(404, response.length());
				} else {
					t.sendResponseHeaders(200, response.length());
				}
			} else if (t.getRequestMethod().equals("POST")) {
				response = responseHandler(uriString, requestString);
				t.sendResponseHeaders(200, response.length());
			}

			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

}
