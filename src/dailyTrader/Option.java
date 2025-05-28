package dailyTrader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Random;

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
	String type;

	public Option(JSONObject obj) {
		symbol = obj.getString("symbol");
		strikePrice = obj.getDouble("strike_price");
		underlyingSymbol = obj.getString("underlying_symbol");
		name = obj.getString("name");
		type = obj.getString("type");
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


	double getMidPrice() {
		return (askPrice + bidPrice) / 2.0;
	}

	double calculateIVFromPrice(double price) {
		int c = 0;
		double vol = 0.0;
		double priceGuess = 0.0;
		while (priceGuess < price) {
			c++;
			priceGuess = getPriceAtIV(vol);
			vol += 0.01;
			if (c > 10000) {
				System.err.println("IV calculation for: " + symbol + " Failed!");
				break;
			}
		}
		vol = Math.round(vol * 100.0) / 100.0;
		return vol;
	}

	double getExpiryYearFraction() {
		return (double) ((Duration.between(Instant.now(), expiration.toInstant()).toSeconds()) / 60.0 / 60.0 / 24.0
				/ 252.0);
	}
	
    public double callPrice(double s, double x, double r, double sigma, double t) {
        double d1 = (Math.log(s/x) + (r + sigma * sigma/2) * t) / (sigma * Math.sqrt(t));
        double d2 = d1 - sigma * Math.sqrt(t);
        return s * cdf(d1) - x * Math.exp(-r*t) * cdf(d2);
    }
    
    public double putPrice(double s, double x, double r, double sigma, double t) {
        double d1 = (Math.log(s/x) + (r + sigma * sigma/2) * t) / (sigma * Math.sqrt(t));
        double d2 = d1 - sigma * Math.sqrt(t);
        return s * cdf(d1) - x * Math.exp(-r*t) * cdf(d2);
    }
    
	public double getPriceAtIV(double vol) {
		double t = getExpiryYearFraction();
		if (type.equals("call")) {
			return callPrice(closePrice, strikePrice, 0.01, vol, t);
		} else {
			return putPrice(closePrice, strikePrice, 0.01, vol, t);
		}
	}

	
	
	double cdf(double z) {
        return 1.0/(1.0 + Math.pow(Math.E, -1.65*z));
	}

	public double getValueAtExpiry(double underlyingPrice) {
		double price = underlyingPrice - strikePrice;
		return Math.max(0, price);
	}

	public double getProbabilityOfProfit(double entryPrice) {
		double probability = 0;
		double vol = 0.37;
		double t = getExpiryYearFraction();
		double volInPeriod = t * vol;
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			double testPrice = r.nextGaussian() * (volInPeriod * entryPrice) + entryPrice;
			probability += getProfitAtExpiry(testPrice, entryPrice);
		}
		return probability / 10.0;
	}

	public double getProfitAtExpiry(double underlyingPrice, double entryPrice) {
		return (getValueAtExpiry(underlyingPrice) - entryPrice) * 100;
	}


	public String toString() {
		String s = "";
		s += "Name: " + name + "\n";
		s += "Symbol:" + symbol + "\n";
		s += "Strike Price: " + Double.toString(strikePrice) + "\n";
		s += "Expiration Date: " + expiration.toString() + "\n";
		s += "Open interest: " + Integer.toString(openInterest) + "\n";
		if (askPrice != 0) {
			s += "Ask Price: " + Double.toString(Math.round(askPrice * 100) / 100.0) + "\n";
			s += "Bid Price: " + Double.toString(Math.round(bidPrice * 100) / 100.0) + "\n";
			s += "Last Quote: " + Double.toString(closePrice) + " at " + lastQuote.toString() + "\n";
			s += "IV: " + Double.toString(iv) + "\n";
		}
		s += "\n";
		return s;
	}
}
