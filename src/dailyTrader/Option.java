package dailyTrader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class Option {
	String symbol;
	double strikePrice;
	int openInterest;
	Date expiration;
	double bidPrice = 0;
	double askPrice = 0;
	double closePrice = 0;
	Date lastQuote;
	String name;
	String underlyingSymbol;
	double iv;

	public Option(JSONObject obj) {
		symbol = obj.getString("symbol");
		strikePrice = obj.getDouble("strike_price");
		underlyingSymbol = obj.getString("underlying_symbol");
		name = obj.getString("name");
		try {
			openInterest = obj.getInt("open_interest");
		} catch (JSONException e) {
			openInterest = 0;
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			expiration = formatter.parse(obj.getString("expiration_date"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void updateFromQuote(double askPrice, double bidPrice, Date lastQuote, double closePrice) {
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.lastQuote = lastQuote;
		this.closePrice = closePrice;
		this.iv = calculateIVFromPrice(getMidPrice());
	}

	double getImpliedVolatility(double price) {
		double iv = 0;
		
		return iv;
	}
	
	double getMidPrice() {
		return (askPrice + bidPrice) / 2.0;
	}
	
	double calculateIVFromPrice(double price) {
		double iv = 0;
		int c = 0;
		double priceGuess = 0;
		while (priceGuess < price) {
			c++;
			priceGuess = getPriceAtIV(iv);
			iv += 0.01;
			iv = Math.round(iv * 1000.0) / 1000.0;
			if (c > 10000) {
				System.err.println("IV calculation for: " + symbol + " Failed!");
				break;
			}
		}
		return iv;
	}
	
	public double getPriceAtIV(double iv) {
		double vol = Math.sqrt(1 + iv);
		double t = (double) (Duration.between(Instant.now(), expiration.toInstant()).toSeconds()) / 60 / 60 / 24 / 365;
		double d1 = (Math.log(closePrice / strikePrice) + t * ((Math.pow(vol, 2) / 2)) / (vol * Math.sqrt(t)));
		double d2 = d1 - (vol * Math.sqrt(t));
		double callPrice = closePrice * Math.pow(Math.E, -t) * cumND(d1) - strikePrice * Math.pow(Math.E, -t) * cumND(d2);
		return callPrice;
	}
	
	public double getZeroVolatilityPrice() {
		return getPriceAtIV(0);
	}
	
	double cumND(double x) {
		double n = 0;
		double a1 = 0.319381530;
		double a2 = -0.356563782;
		double a3 = 1.781477937;
		double a4 = -1.821255978;
		double a5 = 1.330274429;
		double lambda = 0.2316419;
		double k = 1 / (1 + lambda * (double)x);
		double n_prime = 1 / Math.sqrt(2 * Math.PI) * Math.exp(-x * x / 2);
		if (x >= 0) {
			n = 1 - n_prime
					* (a1 * k + a2 * Math.pow(k, 2) + a3 * Math.pow(k, 3) + a4 * Math.pow(k, 4) + a5 * Math.pow(k, 5));
		} else {
			n = 1 - cumND(-x);
		}
		return (double) n;
	}

	public double getValueAtExpiry(double underlyingPrice) {
		double price = underlyingPrice - strikePrice;
		return Math.max(0, price);
	}

	public double getProbabilityOfProfitAtExpiry(double underlyingPrice, double entryPrice) {
		double probability = 0;
		
		return probability;
	}
	
	public double getProfitAtExpiry(double underlyingPrice, double entryPrice) {
		return (getValueAtExpiry(underlyingPrice) - entryPrice) * 100;
	}

	public double normalDistribution(double x, double u, double o) {
		double result = 0;
		result = (double) ((1 / (Math.sqrt(2 * Math.PI * o * o))) * Math.pow(Math.E, -Math.pow(x - u, 2) / (2 * o * o)));
		return result;
	}

	public String toString() {
		String s = "";
		s += "Name: " + name + "\n";
		s += "Symbol:" + symbol + "\n";
		s += "Strike Price: " + Double.toString(strikePrice) + "\n";
		s += "Expiration Date: " + expiration.toString() + "\n";
		s += "Open interest: " + Integer.toString(openInterest) + "\n";
		if (askPrice != 0) {
			s += "Ask Price: " + Double.toString(askPrice) + "\n";
			s += "Bid Price: " + Double.toString(bidPrice) + "\n";
			s += "Last Quote: " + Double.toString(closePrice) + " at " + lastQuote.toString() +"\n";
			s += "IV: " + Double.toString(iv) + "\n";
			s += "Zero IV price: " + Double.toString(getZeroVolatilityPrice()) + "\n";
		}
		s += "\n";
		return s;
	}
}
