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

	static String readHTML(String htmlPath) {
		try {
			return Files.readString(Paths.get(htmlPath));
		} catch (IOException e) {
			return null;
		}
	}
	
	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			System.out.println(t.getRequestMethod());
			String uriString  = t.getRequestURI().toString().substring(1); 
			InputStream inputStream = t.getRequestBody();
			Scanner s = new Scanner(inputStream).useDelimiter("\\A");
			String result = s.hasNext() ? s.next() : "";
			System.out.println(result);
			System.out.println(uriString);
			String response;
			response = readHTML(uriString); //FIX LATER MASSIVE SECURITY ISSUE
			System.out.println(response);
			if (response == null){
				response = readHTML("pages/404.html");
				t.sendResponseHeaders(404, response.length());
			}
			else {
				t.sendResponseHeaders(200, response.length());
			}
			
			OutputStream os = t.getResponseBody();
			System.out.println(t.getResponseCode());
			os.write(response.getBytes());
			os.close();
		}
	}

}
