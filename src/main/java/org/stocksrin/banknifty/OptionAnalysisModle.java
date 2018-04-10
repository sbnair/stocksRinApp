package org.stocksrin.banknifty;

import java.util.ArrayList;
import java.util.List;

public class OptionAnalysisModle {

	private List<Double> maxPains = new ArrayList<>();
	private Integer totalCE;
	private Integer totalPE;

	public List<Double> getMaxPains() {
		return maxPains;
	}

	public void setMaxPains(List<Double> maxPains) {
		this.maxPains = maxPains;
	}

	public Integer getTotalCE() {
		return totalCE;
	}

	public void setTotalCE(Integer totalCE) {
		this.totalCE = totalCE;
	}

	public Integer getTotalPE() {
		return totalPE;
	}

	public void setTotalPE(Integer totalPE) {
		this.totalPE = totalPE;
	}

	@Override
	public String toString() {
		return "OptionAnalysisModle [maxPains=" + maxPains + ", totalCE=" + totalCE + ", totalPE=" + totalPE + "]";
	}

}
