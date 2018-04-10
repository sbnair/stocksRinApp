package org.stocksrin.banknifty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.stocksrin.option.model.OptionModle;
import org.stocksrin.option.model.OptionModles;
import org.stocksrin.utils.APPConstant;
import org.stocksrin.utils.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BankNiftyDataFileUtils {

	public static void main(String[] args) {
		try {
			List<OptionModles> lst = getBankNiftyAllData("1MAR2018");
			System.out.println(lst.size());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<OptionModles> getBankNiftyAllData(String expiry) throws Exception {
		List<OptionModles> lst = new ArrayList<>();

		String dir = APPConstant.STOCKSRIN_NSE_CONF_DIR_BANKNIFTY + expiry;

		List<String> files = FileUtils.getSortedFileNamesfromFolder(new File(dir));

		for (String string : files) {
			System.out.println("Files " + dir + File.separator + string);
			OptionModles optionModles = getBankNiftyDataFromFile(dir + File.separator + string + ".json");
			lst.add(optionModles);
		}

		return lst;
	}

	public static OptionModles getBankNiftyDataFromFile(String fileName) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		// JSON from file to Object
		OptionModles obj = mapper.readValue(new File(fileName), OptionModles.class);
		String spot = getSpot(obj.getUnderlyingSpotPrice());
		obj.setSpot(spot);
		filterOptionData(obj);
		return obj;
	}

	private static String getSpot(String data) {
		String a[] = data.split("BANKNIFTY");
		String b[] = a[1].split(" ");
		return b[1].substring(0, 8).trim();
	}

	public static void filterOptionData(OptionModles optionModles) {

		String s = optionModles.getSpot();
		double spot = Double.parseDouble(s);
		double put = -1200;
		List<OptionModle> optionModle = optionModles.getOptionModle();
		List<OptionModle> newLst = new ArrayList<>();

		for (OptionModle optionModle2 : optionModle) {
			double strike = Double.parseDouble(optionModle2.getStrike_price());
			double diff = spot - strike;
			if (diff < 1200 && diff > put) {
				newLst.add(optionModle2);

			}
		}

		optionModles.setOptionModle(newLst);
	}

}
