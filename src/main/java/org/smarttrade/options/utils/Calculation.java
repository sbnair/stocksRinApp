package org.smarttrade.options.utils;

import java.util.ArrayList;
import java.util.List;

import org.option.currency.models.MaxPain;
import org.option.currency.models.MaxPains;
import org.stocksrin.option.common.model.OptionModle;

public class Calculation {

	public static MaxPains calMaxPain(List<OptionModle> optionModle, Double strickDiff, String expiry) {

		MaxPains maxPains = new MaxPains();
		List<MaxPain> maxPainList = new ArrayList<>();

		for (int i = 0; i < optionModle.size(); i++) {
			Double strikePrice = optionModle.get(i).getStrike_price();

			Integer ceOI = optionModle.get(i).getC_oi();
			Integer peOI = optionModle.get(i).getP_oi();

			MaxPain maxPain = new MaxPain(strikePrice, ceOI, peOI);
			Double total = 0.0;

			Double callCuresult = 0.0;
			for (int j = 0; j < i; j++) {
				Integer a1 = optionModle.get(j).getC_oi();
				if (a1 != null) {
					callCuresult = callCuresult + (a1 * ((strickDiff * i) - (strickDiff * j)));
					maxPain.setCumulativeCe(callCuresult);
				}
			}

			Double putCuresult = 0.0;
			for (int j = i; j < optionModle.size(); j++) {
				Integer a1 = optionModle.get(j).getP_oi();
				if (a1 != null) {
					putCuresult = putCuresult + (a1 * ((strickDiff * j) - (strickDiff * i)));
					maxPain.setCumulativePe(putCuresult);
				}

			}

			total = putCuresult + callCuresult;
			maxPain.setTotal(total);
			maxPain.setStrickPrice(strikePrice);
			maxPainList.add(maxPain);

		}

		maxPains.setDataSet(maxPainList);
		Double maxPainStrick = findMaxPain(maxPainList);
		maxPains.setMaxPainStrick(maxPainStrick);
		maxPains.setExpiry(expiry);
		return maxPains;

	}

	/*
	 * private static MaxPains maxPain(List<OptionModle> lst) {
	 * 
	 * MaxPains maxPains = new MaxPains(); Double strickDiff = 0.25;
	 * List<MaxPain> maxPainList = new ArrayList<>(); for (int i = 0; i <
	 * lst.size(); i++) {
	 * 
	 * //String a = lst.get(i).getStrike_Price().trim().substring(1, 6); //
	 * removing // special // character
	 * 
	 * Double a = lst.get(i).getStrike_price();
	 * 
	 * //String b = lst.get(i).getPE_OI().replaceAll(",", "").trim(); //String c
	 * = lst.get(i).getCE_OI().replaceAll(",", "").trim();
	 * 
	 * Integer b = lst.get(i).getP_oi(); Integer c = lst.get(i).getC_oi();
	 * 
	 * if (b.equals("-")) { b = "0"; }
	 * 
	 * if (c.equals("-")) { c = "0"; }
	 * 
	 * MaxPain maxPain = new MaxPain(a, c, b); Double total = 0.0;
	 * 
	 * Double PutCuresult = 0.0; for (int j = i; j < lst.size(); j++) { //String
	 * a1 = lst.get(j).getPE_OI().replaceAll(",", "").trim(); Integer a1 =
	 * lst.get(j).getP_oi();
	 * 
	 * if (a1.equals("-")) { a1 = "0"; }
	 * 
	 * PutCuresult = PutCuresult + ((a1) * ((strickDiff * j) - (strickDiff *
	 * i))); maxPain.setCumulativePe(PutCuresult); } Double CallCuresult = 0.0;
	 * for (int j = 0; j < i; j++) { //String a1 =
	 * lst.get(j).getCE_OI().replaceAll(",", "").trim(); Integer a1 =
	 * lst.get(j).getC_oi();
	 * 
	 * if (a1.equals("-")) { a1 = "0"; } CallCuresult = CallCuresult + ((a1) *
	 * ((strickDiff * i) - (strickDiff * j)));
	 * maxPain.setCumulativeCe(CallCuresult); }
	 * 
	 * total = PutCuresult + CallCuresult; maxPain.setTotal(total);
	 * maxPainList.add(maxPain); } maxPains.setDataSet(maxPainList); Double
	 * maxPainStrick = findMaxPain(maxPainList);
	 * maxPains.setMaxPainStrick(maxPainStrick); return maxPains; }
	 */

	public static Double findMaxPain(List<MaxPain> maxPainList) {
		Double smallest = maxPainList.get(0).getTotal();
		Double strickPrice = 0.0;
		for (MaxPain maxPain : maxPainList) {

			if (smallest > maxPain.getTotal()) {
				smallest = maxPain.getTotal();
				strickPrice = maxPain.getStrickPrice();
			}
		}
		return strickPrice;

	}

}