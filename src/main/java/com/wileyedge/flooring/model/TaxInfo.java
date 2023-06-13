package com.wileyedge.flooring.model;

import java.math.BigDecimal;

public class TaxInfo {

	private String stateAbbreviation;
	private String stateName;
	private BigDecimal taxRate;
	
	public TaxInfo(String stateAbbreviation, String stateName, BigDecimal taxRate) {
		super();
		this.stateAbbreviation = stateAbbreviation;
		this.stateName = stateName;
		this.taxRate = taxRate;
	}

	public String getStateAbbreviation() {
		return stateAbbreviation;
	}

	public String getStateName() {
		return stateName;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	
	@Override
	public String toString() {
		return "State Abbreviation: " + stateAbbreviation + " | State: " + stateName + " | Tax Rate: " + taxRate;
	}
	
	
	
	
	
	
	
}
