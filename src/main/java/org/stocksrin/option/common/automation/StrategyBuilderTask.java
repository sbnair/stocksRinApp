package org.stocksrin.option.common.automation;

import java.io.File;
import java.util.List;
import java.util.TimerTask;

import org.stocksrin.option.common.priceUtils;
import org.stocksrin.option.common.model.OptionModle;
import org.stocksrin.option.common.model.OptionModles;
import org.stocksrin.option.common.model.Strategy;
import org.stocksrin.option.common.model.Strategy.UnderLying;
import org.stocksrin.option.common.model.StrategyModel;
import org.stocksrin.option.common.model.StrategyModel.OptionType;
import org.stocksrin.option.nifty.NiftyData;
import org.stocksrin.utils.APPConstant;
import org.stocksrin.utils.FileUtils;

public class StrategyBuilderTask extends TimerTask {

	@Override
	public void run() {
		try {
			priceUtils.fetchData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			createStrategyFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		try {
			priceUtils.fetchData();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(NiftyData.shortedExpiry);
		createStrategyFile();
	}

	public static Strategy createStrategyFile() throws Exception {
		String dir = APPConstant.STOCKSRIN__STRATEGY_AUTO_DIR;
		String strategyName = "Strangle@Monthly-Strategy_NIFTY";
		String header = "strategySerial,expiry,type,strike,close_price,quantity,target,stopLoss,spot_close,desc,status,traded_IV,tradedDate";
		Strategy strategy = buildNiftyStrangle();
		if (strategy == null) {
			return null;
		}
		String file = dir + strategyName + ".csv";
		File f = new File(file);
		if (f.exists()) {
			f.delete();
		}
		FileUtils.makeFile(file);
		FileUtils.appendData(header, file);

		List<StrategyModel> m = strategy.getStrategyModels();
		for (StrategyModel strategyModel : m) {
			FileUtils.appendData(strategyModel.toCSV(), file);
		}
		return strategy;
	}

	private static Strategy buildNiftyStrangle() throws Exception {
		String currentExpiry = NiftyData.shortedExpiry.first();

		currentExpiry = Utils.getNiftyExpiry(NiftyData.shortedExpiry, currentExpiry);

		OptionModles optionModles = NiftyData.optionData.get(currentExpiry);
		if (optionModles == null) {
			return null;
		}
		double atmStrike = Utils.getATMStrike(optionModles, 25);
		double lowerStrike = atmStrike - 50;
		double uperStrike = atmStrike + 50;

		List<OptionModle> lst = optionModles.getOptionModle();
		Strategy strategy = new Strategy(UnderLying.NIFTY);

		int qnt = -75;
		Strategy leg1Put = Utils.buildStrategy("BNF", lst, lowerStrike, OptionType.PUT, currentExpiry, optionModles.getSpot(), qnt);
		Strategy leg1Call = Utils.buildStrategy("BNF", lst, lowerStrike, OptionType.CALL, currentExpiry, optionModles.getSpot(), qnt);

		Strategy leg2Put = Utils.buildStrategy("BNF", lst, atmStrike, OptionType.PUT, currentExpiry, optionModles.getSpot(), qnt);
		Strategy leg2Call = Utils.buildStrategy("BNF", lst, atmStrike, OptionType.CALL, currentExpiry, optionModles.getSpot(), qnt);

		Strategy leg3Put = Utils.buildStrategy("BNF", lst, uperStrike, OptionType.PUT, currentExpiry, optionModles.getSpot(), qnt);
		Strategy leg3Call = Utils.buildStrategy("BNF", lst, uperStrike, OptionType.CALL, currentExpiry, optionModles.getSpot(), qnt);

		strategy.getStrategyModels().addAll(leg1Put.getStrategyModels());
		strategy.getStrategyModels().addAll(leg1Call.getStrategyModels());

		strategy.getStrategyModels().addAll(leg2Put.getStrategyModels());
		strategy.getStrategyModels().addAll(leg2Call.getStrategyModels());

		strategy.getStrategyModels().addAll(leg3Put.getStrategyModels());
		strategy.getStrategyModels().addAll(leg3Call.getStrategyModels());
		return strategy;
	}

}
