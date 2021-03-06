package org.option.currency.tasks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.jsoup.nodes.Document;
import org.option.currency.models.Future;
import org.smarttrade.options.utils.APPConstant;
import org.smarttrade.options.utils.DateUtils;
import org.smarttrade.options.utils.DocumentParser;
import org.stocksrin.option.common.model.OptionModles;
import org.stocksrin.utils.HTMLPageDocumentDownloader;
import org.stocksrin.utils.LoggerSysOut;

public class USDINRDataCollector implements Runnable {

	public static boolean isUpdateTime() {
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("IST"));
		LoggerSysOut.print("Now :" + now.get(Calendar.HOUR_OF_DAY));
		// do not update data when time is more then 6 pm and data is already updated 
		if (now.get(Calendar.HOUR_OF_DAY) > 18) {
			/*if(!USDINRData.getExpiryList().isEmpty()){
				return false;
			}else{
				return true;
			}*/
		}else{
			return true;	
		}
		return false;
	}

	@Override
	public void run() {
		LoggerSysOut.print(" ************ Start Featching Data @ " + DateUtils.getTodayDateTime() + "**************");
		if(isUpdateTime()){

		Document doc = HTMLPageDocumentDownloader.getDocument(APPConstant.NSE_URL_INIT);

		List<String> lst = new ArrayList<String>();
		try {
			lst = DocumentParser.getInstance().getExpiryList(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
/*
		USDINRData.getExpiryList().clear();
		USDINRData.setExpiryList(lst);

		getAllExpirOptionData(lst doc);
		USDINRData.ClearOldData(lst);*/
	
		}else{
			LoggerSysOut.print("Data is already updated for last market ");
		}
		LoggerSysOut.print(" ************ Completed Featching Data @ " + DateUtils.getTodayDateTime() + "**************");
		

	}

	public void getAllExpirOptionData(List<String> expiryList, Document firstExpiryDoc) {
		try {

			// current month expiry
			String firstExpiry = expiryList.get(0);
			String url1 = APPConstant.getUSDIINROptionChainURL(firstExpiry);
			Document firstDoc = HTMLPageDocumentDownloader.getDocument(url1);
			// DocumentParser.getInstance().getOptionChainTable(firstDoc);
			OptionModles columns1 = DocumentParser.getInstance().getOptionData(firstDoc);

			columns1.setExpiryList(expiryList);
			columns1.setExpiry(expiryList.get(0));

			//USDINRData.updateOptionData(expiryList.get(0) columns1);

			String futureUrl = APPConstant.getUSDINRFutureURL(expiryList.get(0));
			//LoggerSysOut.print(futureUrl);
			Document futureDc = HTMLPageDocumentDownloader.getDocument(futureUrl);
			Future futurePrice = DocumentParser.getInstance().getFuturePrice(futureDc);
		
			columns1.setLastDataUpdated(DateUtils.getTodayDateTime());

			// leave first expiry
			for (int i = 1; i < expiryList.size(); i++) {

				String url = APPConstant.getUSDIINROptionChainURL(expiryList.get(i));
				Document doc = HTMLPageDocumentDownloader.getDocument(url);
				// LoggerSysOut.print("File URL " + url);
				OptionModles columns = DocumentParser.getInstance().getOptionData(doc);

				columns.setExpiryList(expiryList);
				columns.setExpiry(expiryList.get(i));

				// LoggerSysOut.print("columns" + columns);
				//USDINRData.updateOptionData(expiryList.get(i) columns);

				String futureUrl2 = APPConstant.getUSDINRFutureURL(expiryList.get(i));
				Document futureDc2 = HTMLPageDocumentDownloader.getDocument(futureUrl2);
				Future futurePrice2 = DocumentParser.getInstance().getFuturePrice(futureDc2);
				
				columns.setLastDataUpdated(DateUtils.getTodayDateTime());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}