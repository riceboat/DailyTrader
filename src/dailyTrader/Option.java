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
	String type;
	Bars bars;

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

	public void updateFromQuote(double askPrice, double bidPrice, Date lastQuote, double closePrice, Bars bars) {
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.lastQuote = lastQuote;
		this.closePrice = closePrice;
		this.iv = calculateIVFromPrice(getMidPrice());
		this.bars = bars;
	}

	double getMidPrice() {
		return (askPrice + bidPrice) / 2.0;
	}

	double calculateIVFromPrice(double price) { // iv is usually only accurate when market is open
		double high = 5.0;
		double low = 0.0;
		while ((high / low) - 1 > 0.01) {
			double vol = (high + low) / 2;
			double priceGuess = getPriceAtIV(vol);
			if (priceGuess > price) {
				high = vol;
			} else {
				low = vol;
			}
		}
		double vol = (high + low) / 2;
		vol = Math.round(vol * 100.0) / 100.0;
		return vol;
	}

	double getExpiryYearFraction() {
		return (double) ((Duration.between(Instant.now(), expiration.toInstant()).toSeconds()) / 60.0 / 60.0 / 24.0
				/ 252.0);
	}

	public double callPrice(double s, double x, double sigma, double t) {
		double d1 = (Math.log(s / x) + (sigma * sigma / 2) * t) / (sigma * Math.sqrt(t));
		double d2 = d1 - sigma * Math.sqrt(t);
		return s * cdf(d1) - x * cdf(d2);
	}

	public double putPrice(double s, double x, double sigma, double t) {
		double d1 = (Math.log(s / x) + (sigma * sigma / 2) * t) / (sigma * Math.sqrt(t));
		double d2 = d1 - sigma * Math.sqrt(t);
		return x * cdf(-d2) - s * cdf(-d1);
	}

	public double getPriceAtIV(double vol) {
		double t = getExpiryYearFraction();
		if (type.equals("call")) {
			return callPrice(closePrice, strikePrice, vol, t);
		} else {
			return putPrice(closePrice, strikePrice, vol, t);
		}
	}

	double cdf(double z) {
		return 1.0 / (1.0 + Math.pow(Math.E, -1.65 * z));
	}

	public double getProfitAtExpiry(double underlyingPrice, double entryPrice) {
		if (type.equals("call")) {
			return Math.max(0, underlyingPrice - strikePrice) * 100 - entryPrice * 100;
		} else {
			return Math.max(0, strikePrice - underlyingPrice) * 100 - entryPrice * 100;
		}
	}

	public double getBreakEvenAtExpiry(double entryPrice) {
		if (type.equals("call")) {
			return strikePrice + entryPrice;
		} else {
			return strikePrice - entryPrice;
		}
	}

	public double getBreakRiskAtExpiry(double entryPrice) {
		if (type.equals("call")) {
			return strikePrice;
		} else {
			return strikePrice;
		}
	}

	double normalDistribution(double x, double std, double mean) {
		return (Math.pow(Math.E, -0.5 * Math.pow((x - mean) / std, 2)) / (std * Math.sqrt(2 * Math.PI)));
	}
	
	
	double getProbabilityOfProfit(double currentPrice) {
		double mean = currentPrice;
		double tot = 0;
		double std = bars.getStandardDeviation();
		if (type.equals("put")) {
			double min = 0;
			double max = getBreakEvenAtExpiry(bidPrice);

			int n = 1000;
			for (int i = 0; i < n; i++) {
				double a = min + (i * max / n);
				double b = min + (i + 1) * (max / n);
				tot += (b - a) * ((normalDistribution(a, std, mean) + normalDistribution(b, std, mean)) / 2.0);
			}
		} else {
			double min = getBreakEvenAtExpiry(bidPrice);
			double max = 1000;
			int n = 1000;
			for (int i = 0; i < n; i++) {
				double a = min + (i * max / n);
				double b = min + (i + 1) * (max / n);
				tot += (b - a) * ((normalDistribution(a, std, mean) + normalDistribution(b, std, mean)) / 2.0);
			}
		}
		tot = Math.round(tot * 1000.0)/10.0;
		return tot;
	}
	double getProbabilityOfMaxLoss(double currentPrice) {
		double mean = currentPrice;
		double tot = 0;
		double std = bars.getStandardDeviation();
		if (type.equals("put")) {
			double min = strikePrice;
			double max = 1000;

			int n = 1000;
			for (int i = 0; i < n; i++) {
				double a = min + (i * max / n);
				double b = min + (i + 1) * (max / n);
				tot += (b - a) * ((normalDistribution(a, std, mean) + normalDistribution(b, std, mean)) / 2.0);
			}
		} else {
			double min = 0;
			double max = strikePrice;
			int n = 1000;
			for (int i = 0; i < n; i++) {
				double a = min + (i * max / n);
				double b = min + (i + 1) * (max / n);
				tot += (b - a) * ((normalDistribution(a, std, mean) + normalDistribution(b, std, mean)) / 2.0);
			}
		}
		tot = Math.round(tot * 1000.0)/10.0;
		return tot;
	}

	double getMaxRisk() {
		return askPrice * 100;
	}

	public String toString() {
		String s = "\n";
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
			s += "Breakeven: " + Double.toString(getBreakEvenAtExpiry(bidPrice)) + "\n";
			s += "Max Risk: " + Double.toString(getMaxRisk()) + "\n";
			s += "Profit Probability: " + Double.toString(getProbabilityOfProfit(closePrice)) + "\n";
			s += "Max Loss Probability: " + Double.toString(getProbabilityOfMaxLoss(closePrice)) + "\n";
		}
		return s;
	}
}
