package com.jamonapi;

public class MonKeyItemBase implements MonKeyItem {
	private String summaryLabel;
	private String detailLabel;

	public MonKeyItemBase(String summaryLabel) {
		this.summaryLabel=summaryLabel;
		this.detailLabel=summaryLabel;
	}
	
	public MonKeyItemBase(String summaryLabel, String detailLabel) {
		this.summaryLabel=summaryLabel;
		this.detailLabel=detailLabel;
	}
	

	public String getDetailLabel() {
		return detailLabel;
	}

	
	/** should call getSummaryLabel */
	public String toString(){
		return summaryLabel;
	}
}
