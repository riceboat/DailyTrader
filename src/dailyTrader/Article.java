package dailyTrader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class Article {
	String headline;
	String url;
	List<Object> symbols;
	Date date;
	String content;

	public Article(JSONObject jsonObject) {
		headline = jsonObject.getString("headline");
		url = jsonObject.getString("url");
		symbols = jsonObject.getJSONArray("symbols").toList();
		content = jsonObject.getString("content").replaceAll("\\<[^>]*>", "");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		try {
			date = formatter.parse(jsonObject.getString("updated_at"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String toString() {
		String s = "\nArticle about " + symbols.toString() + "\n";
		s += "Date Updated: " + date + "\n";
		s += "Headline: " + headline + "\n";
		s += "URL: " + url + "\n";
		s += "Content: " + content + "\n";
		return s;
	}
}
