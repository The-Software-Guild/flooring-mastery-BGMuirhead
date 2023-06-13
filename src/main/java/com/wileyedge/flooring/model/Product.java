package com.wileyedge.flooring.model;

import java.math.BigDecimal;

public class Product {

	private String productType;
	private BigDecimal costPerSquareFoot;
	private BigDecimal labourCostPerSquareFoot;

	public Product(String productType, BigDecimal costPerSquareFoot, BigDecimal labourCostPerSquareFoot) {
		super();
		this.productType = productType;
		this.costPerSquareFoot = costPerSquareFoot;
		this.labourCostPerSquareFoot = labourCostPerSquareFoot;
	}

	public String getProductType() {
		return productType;
	}

	public BigDecimal getCostPerSquareFoot() {
		return costPerSquareFoot;
	}

	public BigDecimal getLabourCostPerSquareFoot() {
		return labourCostPerSquareFoot;
	}

	@Override
	public String toString() {
		return "Product Type: " + productType + " | Cost Per Square Foot: $" + costPerSquareFoot
				+ " | Labour Cost Per Square Foot: $" + labourCostPerSquareFoot;
	}

}
