package com.wileyedge.flooring.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Order {

	private int orderNumber;
	private LocalDate orderDate;
	private String customerName;
	private String state; // is abbreviation
	private BigDecimal taxRate;
	private String productType;
	private BigDecimal area;
	private BigDecimal costPerSquareFoot;
	private BigDecimal labourCostPerSquareFoot;
	private BigDecimal materialCost;
	private BigDecimal labourCost;
	private BigDecimal tax;
	private BigDecimal total;

	public Order(LocalDate orderDate, String customerName, String state, String productType, BigDecimal area) {
		super();
		this.orderDate = orderDate;
		this.customerName = customerName;
		this.state = state;
		this.productType = productType;
		this.area = area;
	}

	public Order(LocalDate orderDate, int orderNumber, String customerName, String state, BigDecimal taxRate,
			String productType, BigDecimal area, BigDecimal costPerSquareFoot, BigDecimal labourCostPerSquareFoot,
			BigDecimal materialCost, BigDecimal labourCost, BigDecimal tax, BigDecimal total) {
		super();
		this.orderDate = orderDate;
		this.orderNumber = orderNumber;
		this.customerName = customerName;
		this.state = state;
		this.taxRate = taxRate;
		this.productType = productType;
		this.area = area;
		this.costPerSquareFoot = costPerSquareFoot;
		this.labourCostPerSquareFoot = labourCostPerSquareFoot;
		this.materialCost = materialCost;
		this.labourCost = labourCost;
		this.tax = tax;
		this.total = total;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public String getProductType() {
		return productType;
	}

	public String getState() {
		return state;
	}

	public String getCustomerName() {
		return customerName;
	}

	public BigDecimal getArea() {
		return area;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public void setArea(BigDecimal area) {
		this.area = area;
	}

	private void calcMaterialCost() {
		BigDecimal cost = this.area.multiply(this.costPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
		this.materialCost = cost;
	}

	private void calcLabourCost() {
		BigDecimal cost = this.area.multiply(this.labourCostPerSquareFoot).setScale(2, RoundingMode.HALF_UP);
		this.labourCost = cost;
	}

	private void calcTax() {
		BigDecimal tax = this.materialCost.add(labourCost).multiply(this.taxRate.divide(new BigDecimal("100")))
				.setScale(2, RoundingMode.HALF_UP);
		this.tax = tax;
	}

	private void calcTotal() {
		BigDecimal total = this.materialCost.add(this.labourCost).add(this.tax).setScale(2, RoundingMode.HALF_UP);
		this.total = total;
	}

	public void updateDetails() {
		calcMaterialCost();
		calcLabourCost();
		calcTax();
		calcTotal();
	}

	public void setProductInfo(Product p) {
		this.productType = p.getProductType();
		this.labourCostPerSquareFoot = p.getLabourCostPerSquareFoot();
		this.costPerSquareFoot = p.getCostPerSquareFoot();
	}

	@Override
	public String toString() {
		return "  * Order Date:  " + orderDate + " | Order Number: " + orderNumber + " | Customer Name: " + customerName
				+ " | State: " + state + " | Tax Rate: " + taxRate + " | Product Type: " + productType + " | Area: "
				+ area + "ft^2 | Cost Per Square Foot: $" + costPerSquareFoot + " | Labour Cost Per Square Foot: $"
				+ labourCostPerSquareFoot + " | Material Cost: $" + materialCost + " | Labour Cost: $" + labourCost
				+ " | Tax: $" + tax + " | Total: $" + total;
	}

	public String showDetails() {
		return "  * | Order Date:  " + orderDate + "\r\n  * | Customer Name: " + customerName + "\r\n  * | State: "
				+ state + "\r\n  * | Tax Rate: " + taxRate + "\r\n  * | Product Type: " + productType
				+ "\r\n  * | Area: " + area + " ft^2 \r\n  * | Cost Per Square Foot: $" + costPerSquareFoot
				+ "\r\n  * | Labour Cost Per Square Foot: $" + labourCostPerSquareFoot + "\r\n  * | Material Cost: $"
				+ materialCost + "\r\n  * | Labour Cost: $" + labourCost + "\r\n  * | Tax: $" + tax
				+ "\r\n  * | Total: $" + total;
	}

	public String formatOrder() {
		return orderNumber + "," + customerName + "," + state + "," + taxRate + "," + productType + "," + area + ","
				+ costPerSquareFoot + "," + labourCostPerSquareFoot + "," + materialCost + "," + labourCost + "," + tax
				+ "," + total;
	}

}
