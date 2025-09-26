package dailyTrader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class JSONManager {
	public JSONManager() {

	}

	public void writeToJSONFile(Bars bars, String filePath) {
		JSONObject json = bars.toJSON();
		try (PrintWriter myFile = new PrintWriter(filePath + ".json", "UTF-8")) {
			myFile.println(json);
			myFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void writeToJSONFile(Market market, String filePath) {
		JSONObject json = market.toJSON();
		try (PrintWriter myFile = new PrintWriter(filePath + ".json", "UTF-8")) {
			myFile.println(json);
			myFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	String readJSONFile(String filePath) {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(filePath));
			return new String(encoded, Charset.defaultCharset());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	public Market readMarketFromFile(String filePath) {
		JSONObject marketJSON = new JSONObject(readJSONFile(filePath));
		return new Market(marketJSON);
	}
}
