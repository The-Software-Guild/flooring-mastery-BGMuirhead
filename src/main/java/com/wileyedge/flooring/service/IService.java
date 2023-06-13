package com.wileyedge.flooring.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.wileyedge.flooring.exceptions.InvalidAreaException;
import com.wileyedge.flooring.exceptions.InvalidDateException;
import com.wileyedge.flooring.exceptions.InvalidNameException;
import com.wileyedge.flooring.exceptions.InvalidStateException;
import com.wileyedge.flooring.exceptions.NoOrdersForDateException;
import com.wileyedge.flooring.exceptions.OrderNotFoundException;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

public interface IService {

	boolean initialise();

	List<Order> getOrdersByDate(LocalDate date);

	int addOrder(Order order);

	boolean editOrder(Order order);

	boolean removeOrder(Order order);

	boolean exportData();

	List<Product> getProducts();
	List<TaxInfo> getTaxes();

	boolean validateOrderDate(LocalDate date) throws InvalidDateException;

	boolean checkOrderDateExists(LocalDate date) throws NoOrdersForDateException;

	boolean validateName(String name) throws InvalidNameException;

	boolean validateState(String state) throws InvalidStateException;

	boolean validateArea(BigDecimal area) throws InvalidAreaException;

	Order getOrder(LocalDate date, int orderNumber) throws OrderNotFoundException;

	

	Order configureOrder(Order order, String customerName, String state, Product product, BigDecimal Area);

	Order configureOrder(Order order);

	Order getOrderToDelete(LocalDate date, int orderNumber) throws OrderNotFoundException;

}
