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

	JSONObject readJSONFile(String filePath) throws FileNotFoundException {
		byte[] encoded;
		try {
			encoded = Files.readAllBytes(Paths.get(filePath));
			return new JSONObject(new String(encoded, Charset.defaultCharset()));
		} catch (IOException e) {
			throw new FileNotFoundException("JSON File not Found at: " + filePath);
		}

	}

	public Market readMarketFromFile(String filePath) throws FileNotFoundException {
		JSONObject marketJSON = new JSONObject(readJSONFile(filePath));
		return new Market(marketJSON);
	}

	public String toJSONString(JSONConvertible object) {
		return object.toJSON().toString();
	}

	public void toJSONFile(JSONConvertible object, String filePath) {
		try (PrintWriter myFile = new PrintWriter(filePath, "UTF-8")) {
			myFile.println(object.toJSON());
			myFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public Portfolio readPortfolioFromFile(String filePath) throws FileNotFoundException {
		JSONObject portfolioJSON = new JSONObject(readJSONFile(filePath));
		return new Portfolio(portfolioJSON, new Account(portfolioJSON.getJSONObject("account")));
	}
}
