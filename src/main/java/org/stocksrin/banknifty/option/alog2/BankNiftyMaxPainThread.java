package org.stocksrin.banknifty.option.alog2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import org.stocksrin.banknifty.BankNiftyData;
import org.stocksrin.utils.CommonUtils;
import org.stocksrin.utils.DateUtils;
import org.stocksrin.utils.LoggerSysOut;

@Singleton
public class BankNiftyMaxPainThread extends TimerTask {

	private List<Timer> allTimers = new ArrayList<>(2);

	@Override
	public void run() {
		LoggerSysOut.print("BankNiftyMaxPainThread  starting");
		if (!DateUtils.isWeekEndDay()) {
			if (CommonUtils.getEveningTime()) {
				try {
					allTimers.clear();
					BankNiftyData.dailyMorningCleanData();
					Timer timer = null;
					timer = new Timer();
					timer.scheduleAtFixedRate(new OptionDataCollectorTask2(), 0l, 600000);
					allTimers.add(timer);
					OptionTask.addTimerTask(timer);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				LoggerSysOut.print("");
			}
		}
	}

	public List<Timer> getAllTimers() {
		return allTimers;
	}

	@PreDestroy
	public void shutDown() {
		for (Timer timer : allTimers) {
			timer.cancel();
		}
	}
}
