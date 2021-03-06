package org.option.currency.usdinr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.stocksrin.option.common.model.OptionModles;

public class UsdInrData {

	private static List<String> expiry = new ArrayList<String>(Arrays.asList("29MAY2017", "28JUN2017", "27JUL2017"));
	// map of Expire Date and option chain for that particular Expire
	private static ConcurrentHashMap<String, OptionModles> data = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<String, OptionModles> getData() {
		return data;
	}

	public static void setData(ConcurrentHashMap<String, OptionModles> data) {
		UsdInrData.data = data;
	}

	public static List<String> getExpiry() {
		return expiry;
	}

	public static void setExpiry(List<String> expiry) {
		UsdInrData.expiry = expiry;
	}
}
