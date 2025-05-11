package dailyTrader;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
	APIManager apiManager;
	DecisionEngine decisionEngine;
	Instant marketOpen;
	Instant marketClose;
	boolean isOpen;
	ScheduledExecutorService schedule;
	
	public Scheduler(APIManager apiManager, DecisionEngine decisionEngine) {
		this.apiManager = apiManager;
		marketOpen = apiManager.getNextMarketOpen().toInstant();
		marketClose = apiManager.getNextMarketClose().toInstant();
	}

	public void frame() {
		isOpen = apiManager.isMarketOpen();
		if (isOpen) {
			Date now = Date.from(Instant.now());
			System.out.println(now);
			decisionEngine.frame();
		} else {
			schedule.shutdown();
		}
	}

	public void notReady() {
		Duration waitTime = Duration.between(Instant.now(), marketOpen);
		System.out.println("Waiting for " + Long.toString(waitTime.toMinutes()) + " Minutes");
		try {
			TimeUnit.SECONDS.sleep(waitTime.toSeconds());
			start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		
		isOpen = apiManager.isMarketOpen();
		schedule = Executors.newScheduledThreadPool(1);

		Runnable tt = () -> {
			frame();
		};

		if (isOpen) {
			schedule.scheduleAtFixedRate(tt, 0, 1, TimeUnit.MINUTES);
		} else {
			marketOpen = apiManager.getNextMarketOpen().toInstant();
			
			notReady();
		}

	}
}
