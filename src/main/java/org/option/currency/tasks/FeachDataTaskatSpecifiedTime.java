package org.option.currency.tasks;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.stocksrin.utils.LoggerSysOut;

public class FeachDataTaskatSpecifiedTime extends TimerTask {

	public static void main(String[] args) {
		
		Calendar today = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		//today.set(Calendar.HOUR_OF_DAY 16);
		//today.set(Calendar.MINUTE 11);
		//today.set(Calendar.SECOND 0);

		LoggerSysOut.print(today.get(Calendar.HOUR_OF_DAY));
		LoggerSysOut.print(today.get(Calendar.MINUTE));
		// every night at 2am you run your task
		Timer timer = new Timer();
		timer.schedule(new FeachDataTaskatSpecifiedTime(), today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // 60*60*24*100
																																// =
																																// 8640000ms

		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		LoggerSysOut.print("today" + now.get(Calendar.HOUR_OF_DAY));
		
		if (now.get(Calendar.HOUR_OF_DAY) > 15) {
			LoggerSysOut.print("time is more then 4 pm");
		}
	}

	@Override
	public void run() {
		LoggerSysOut.print("Executing Task");

	}
}
