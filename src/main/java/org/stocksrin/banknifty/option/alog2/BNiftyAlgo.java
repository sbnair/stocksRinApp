package org.stocksrin.banknifty.option.alog2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.option.currency.models.Column;
import org.option.currency.models.Columns;
import org.stocksrin.banknifty.BankNiftyData;
import org.stocksrin.banknifty.BankNiftyUtils;
import org.stocksrin.banknifty.TickData;
import org.stocksrin.banknifty.TickData.Type;
import org.stocksrin.banknifty.option.alog.BNiftyOptionLastDayTrade;
import org.stocksrin.banknifty.option.alog.ContinuesOptionPriceGetter;
import org.stocksrin.banknifty.option.alog2.StrategyModel.OptionType;
import org.stocksrin.email.SendEmail;
import org.stocksrin.utils.CommonUtils;

public class BNiftyAlgo {

	private static double targetProfit = 800;
	private static double targetLoss = -800;
	public static boolean flag = true;

	public static synchronized List<TickData> getData(List<StrategyModel> strategyModels, String expiry) throws Exception {

		Columns columns = BankNiftyData.getBankNiftyCurrentTimeData2().get(expiry);
		List<TickData> values = new ArrayList<>(3);
		List<Column> data = columns.getDataset();
		double bankNiftySpotPrice = BankNiftyUtils.getLSpotPriceUnderlyingSpotPrice(columns.getUnderlyingSpotPrice());
		String datatime = BankNiftyUtils.getLastUpdatedPriceFromUnderlyingSpotPrice(columns.getUnderlyingSpotPrice());

		for (StrategyModel strategyModel2 : strategyModels) {

			for (Column column : data) {

				double strikePrice = Double.parseDouble(column.getStrike_Price());

				if (strikePrice == strategyModel2.getStrike()) {

					if (strategyModel2.getType().equals(OptionType.PUT)) {
						TickData tickData = new TickData(TickData.Type.PUT, strategyModel2.getStrike(), Float.parseFloat(column.getPE_LTP()), Float.parseFloat(column.getPE_Net_Change()),
								strategyModel2.getClose_price(), bankNiftySpotPrice, strategyModel2.getSpot_close());
						tickData.setQuantity(strategyModel2.getQuantity());
						tickData.setLastUpdateTime(datatime);
						values.add(tickData);
					} else {
						TickData tickData = new TickData(TickData.Type.CALL, strategyModel2.getStrike(), Float.parseFloat(column.getCE_LTP()), Float.parseFloat(column.getCE_Net_Change()),
								strategyModel2.getClose_price(), bankNiftySpotPrice, strategyModel2.getSpot_close());
						tickData.setQuantity(strategyModel2.getQuantity());
						tickData.setLastUpdateTime(datatime);
						values.add(tickData);
					}
				}
			}
		}

		return values;
	}

	private static void targetMail(Double totalPandL, String string) {
		if (totalPandL > targetProfit) {

			SendEmail.sentMail(" Profit " + totalPandL, string);

			System.out.println("Target Achived of : " + targetProfit);
			targetProfit = targetProfit + 200;
			System.out.println("Next target is Target : " + targetProfit);
		} else if (totalPandL < targetLoss) {

			SendEmail.sentMail(" Loss " + totalPandL, string);
			targetLoss = targetLoss - 200;
		}
		if (flag) {
			SendEmail.sentMail("Market Open Status:" + totalPandL, string);
			flag = false;
		}

	}

	public static StringBuilder algo2(List<TickData> tickDatas, List<StrategyModel> lst) {
		StringBuilder string = new StringBuilder();

		DecimalFormat df = new DecimalFormat("#00.00");
		TickData dat = tickDatas.get(0);
		double bankNiftyDiff = dat.getUnderlyingIndexSpotPrice() - dat.getUnderlyingIndexPreviousDayClosePrice();

		MaxMinValues.setMaxMinForBankNIftyPoint(bankNiftyDiff);
		MaxMinValues.setMaxMinForBankNIfty(dat.getUnderlyingIndexSpotPrice());

		String bankNIftyRange = "[" + df.format(MaxMinValues.bankNiftyMinPoint) + " -- " + df.format(MaxMinValues.bankNiftyMaxPoint) + "]";

		double totalSellPoints = 0.00;
		double totalltp = 0.00;
		double totalPL = 0.00;

		string.append(
				"Time: " + dat.getLastUpdateTime() + "              BankNifty Spot : " + dat.getUnderlyingIndexSpotPrice() + " [" + df.format(bankNiftyDiff) + "]" + ", L/H:" + bankNIftyRange + "\n");
		string.append("-------------------------------------------------------------------------------------" + "\n");
		string.append("type      sell      ltp        P&L        change      Qty      strike      strikeDiff" + "\n");
		string.append("-------------------------------------------------------------------------------------" + "\n");

		for (TickData tickData : tickDatas) {

			String type = null;
			if (tickData.getType().equals(Type.PUT)) {
				type = "PUT ";
			} else {
				type = "CALL";
			}
			double strikediff = tickData.getStrike() - tickData.getUnderlyingIndexSpotPrice();
			double pl = (tickData.getLtp() - tickData.getPreviousDayClosePrice()) * tickData.getQuantity();
			totalPL = totalPL + pl;
			totalSellPoints = totalSellPoints + tickData.getPreviousDayClosePrice();
			totalltp = totalltp + tickData.getLtp();
			double myTradechange = tickData.getLtp() - tickData.getPreviousDayClosePrice();
			string.append(type + "      " + df.format(tickData.getPreviousDayClosePrice()) + "     " + df.format(tickData.getLtp()) + "      " + df.format(pl) + "     " + df.format(myTradechange)
					+ "     " + tickData.getQuantity() + "      " + tickData.getStrike() + "      " + df.format(strikediff) + "\n");
		}

		string.append("-------------------------------------------------------------------------------------" + "\n");
		double mytradechnage = totalltp - totalSellPoints;

		if (totalPL > MaxMinValues.maxProfit) {
			MaxMinValues.maxProfit = totalPL;

		}
		if (totalPL < MaxMinValues.minProfit) {
			MaxMinValues.minProfit = totalPL;
		}

		String profitRange = "[ " + df.format(MaxMinValues.minProfit) + " -- " + df.format(MaxMinValues.maxProfit) + "]";
		string.append("Total     " + df.format(totalSellPoints) + "     " + df.format(totalltp) + "      " + df.format(totalPL) + "      " + df.format(mytradechnage) + "       " + "P&LHighLow "
				+ profitRange + "\n");
		string.append("-------------------------------------------------------------------------------------" + "\n");
		System.out.println(string);
		targetMail(totalPL, string.toString());
		return string;
	}

	public StringBuilder algo(List<TickData> tickData, BNiftyOptionLastDayTrade bNiftyOptionLastDayTrade) {

		TickData putData = tickData.get(0);
		TickData callData = tickData.get(1);

		double totalSellPoints = bNiftyOptionLastDayTrade.getPut_close_price() + bNiftyOptionLastDayTrade.getCall_close_price();
		double totalltp = putData.getLtp() + callData.getLtp();
		double totalchange = putData.getChange() + callData.getChange();
		DecimalFormat df = new DecimalFormat("###.##");

		double putProfit = (bNiftyOptionLastDayTrade.getPut_close_price() - putData.getLtp()) * 40;
		double callProfit = (bNiftyOptionLastDayTrade.getCall_close_price() - callData.getLtp()) * 40;

		double totalPandL = putProfit + callProfit;

		double bniftyDiff = putData.getUnderlyingIndexSpotPrice() - bNiftyOptionLastDayTrade.getSpot_close();

		String bniftyDiff2 = df.format(bniftyDiff);

		double putstrikeDiff = putData.getUnderlyingIndexSpotPrice() - putData.getStrike();
		double callstrikeDiff = callData.getStrike() - callData.getUnderlyingIndexSpotPrice();

		StringBuilder string = new StringBuilder();
		string.append("                              BankNifty Spot : " + putData.getUnderlyingIndexSpotPrice() + " [" + bniftyDiff2 + "] " + "\n");
		string.append("-------------------------------------------------------------------" + "\n");
		string.append("type     sell     ltp     change    P&L     strike    strikeDiff" + "\n");
		string.append("-------------------------------------------------------------------" + "\n");
		string.append("PUT      " + bNiftyOptionLastDayTrade.getPut_close_price() + "     " + putData.getLtp() + "    " + putData.getChange() + "    " + df.format(putProfit) + "      "
				+ putData.getStrike() + "    " + df.format(putstrikeDiff) + "\n");
		string.append("CALL     " + bNiftyOptionLastDayTrade.getCall_close_price() + "     " + callData.getLtp() + "    " + callData.getChange() + "    " + df.format(callProfit) + "      "
				+ callData.getStrike() + "    " + df.format(callstrikeDiff) + "\n");
		string.append("-------------------------------------------------------------------" + "\n");
		string.append("Total    " + totalSellPoints + "    " + df.format(totalltp) + "    " + df.format(totalchange) + "    [" + df.format(totalPandL) + "]" + "   " + "P&LHighLow " + "\n");
		string.append("-------------------------------------------------------------------" + "\n");

		System.out.println(string);

		return string;
	}

	public static void main(String[] args) throws InterruptedException {

		List<BNiftyOptionLastDayTrade> lastDayTrade = CommonUtils.getBankNiftyLastTradeData2();
		ContinuesOptionPriceGetter continuesOptionPriceGetter = new ContinuesOptionPriceGetter(lastDayTrade.get(0));
		continuesOptionPriceGetter.fetchOptionData();

	}

}
