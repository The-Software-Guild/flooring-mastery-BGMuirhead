package com.wileyedge.flooring.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

public interface IView {

	int printMenu();

	void displayOrders(List<Order> orders);

	int getOrderNumber();

	String editName(Order order);

	String editState(Order order);

	Product editProductType(Order order, List<Product> products);

	BigDecimal editArea(Order order);

	LocalDate getOrderDate();

	String getCustomerName();

	String getCustomerState();

	Product displayProducts(List<Product> products);

	BigDecimal getOrderArea();

	boolean confirmOrder(Order order);

	boolean confirmUpdate(Order order);

	boolean confirmDelete(Order order);

	void displayOrderNumber(int orderNumber);

	void displayOrder(Order order);

	void printTaxInfo(List<TaxInfo> taxes);

	void printSpacer();
}
