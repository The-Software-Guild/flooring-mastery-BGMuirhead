package com.wileyedge.flooring.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.wileyedge.flooring.dao.Dao;
import com.wileyedge.flooring.dao.IDao;
import com.wileyedge.flooring.exceptions.InvalidAreaException;
import com.wileyedge.flooring.exceptions.InvalidDateException;
import com.wileyedge.flooring.exceptions.InvalidNameException;
import com.wileyedge.flooring.exceptions.InvalidStateException;
import com.wileyedge.flooring.exceptions.NoOrdersForDateException;
import com.wileyedge.flooring.exceptions.OrderNotFoundException;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

@Component(value = "service")
public class Service implements IService {

	@Autowired
	private IDao dao;
	private List<Product> products;
	private List<TaxInfo> taxes;
	private int nextOrderNum;
	private Map<LocalDate, List<Order>> map;

	// Only made for testing purposes
	public Service(Dao dao) {
		super();
		this.dao = dao;
	}

	@Override
	public boolean initialise() {

		// Initialize list of tax info
		taxes = dao.getTaxInfo();

		// Initialize list of products
		products = dao.getProducts();

		// Initialize map of orders
		map = dao.getOrders();
		// Initialize next order num
		nextOrderNum = dao.getMaxOrderNumber() + 1;

		return false;
	}

	@Override
	public List<Order> getOrdersByDate(LocalDate date) {

		return map.get(date);
	}

	@Override
	public int addOrder(Order order) {

		order.setOrderNumber(nextOrderNum);
		nextOrderNum++;

		LocalDate date = order.getOrderDate();

		// get list of orders for that day
		List<Order> orders = map.get(date);

		// if this is the first order for that day
		if (orders == null) {
			orders = new ArrayList<>();

		}
		// add the order to order list and to map of orders
		orders.add(order);
		map.put(date, orders);

		dao.saveChanges(date, map.get(date)); // saves the changes to file
		return order.getOrderNumber();
	}

	@Override
	public boolean editOrder(Order order) {

		LocalDate date = order.getOrderDate();
		int orderNumber = order.getOrderNumber();

		List<Order> orders = map.get(date); // get the orders for the order date

		int index = -1;

		// find the index of the order in the list of orders
		for (Order ord : orders) {
			if (ord.getOrderNumber() == orderNumber) {
				index = orders.indexOf(ord);
				break;
			}
		}

		orders.set(index, order); // overwrite the order in the list of orders
		// add to map of orders and save
		map.put(date, orders);
		dao.saveChanges(date, map.get(date));

		return true;
	}

	@Override
	public boolean removeOrder(Order order) {
		LocalDate date = order.getOrderDate();
		int orderNumber = order.getOrderNumber();

		List<Order> orders = map.get(date);

		int index = -1;

		for (Order ord : orders) {
			if (ord.getOrderNumber() == orderNumber) {
				index = orders.indexOf(ord);
				break;
			}
		}

		orders.remove(index); // remove the order from the orders and save changes
		dao.saveChanges(order.getOrderDate(), map.get(order.getOrderDate()));
		return true;
	}

	@Override
	public boolean exportData() {

		dao.exportOrders(map);
		return false;
	}

	@Override
	public List<Product> getProducts() {

		return products;
	}

	@Override
	public boolean checkOrderDateExists(LocalDate date) throws NoOrdersForDateException {

		List<Order> orders = map.get(date);

		if (orders == null || orders.size() == 0) {
			throw new NoOrdersForDateException("There are no orders for that date");
		} else {
			return true;
		}

	}

	@Override
	public boolean validateOrderDate(LocalDate date) throws InvalidDateException {
		LocalDate currDate = LocalDate.now();

		if (currDate.isBefore(date)) {
			return true;
		} else {
			throw new InvalidDateException("Order date must be in the future");
		}

	}

	@Override
	public boolean validateName(String name) throws InvalidNameException {
		Pattern p = Pattern.compile("[a-zA-Z0-9., ]+");
		Matcher m = p.matcher(name);
		boolean test = m.matches();

		if (test) {
			return true;
		} else {
			throw new InvalidNameException("Name does not meet format specifications");
		}

	}

	@Override
	public boolean validateState(String state) throws InvalidStateException {
		for (TaxInfo ti : taxes) {
			if (ti.getStateAbbreviation().equals(state)) {
				return true;
			}
		}
		throw new InvalidStateException("Invalid input or state is unsupported");
	}

	@Override
	public boolean validateArea(BigDecimal area) throws InvalidAreaException {
		try {

			if (area.compareTo(new BigDecimal("100.00")) < 0) {
				throw new InvalidAreaException("Area must be at least 100.00");
			}
		} catch (NullPointerException e) {
			throw new InvalidAreaException("Area must be at least 100.00");
		}

		return true;
	}

	@Override
	public Order getOrder(LocalDate date, int orderNumber) throws OrderNotFoundException {

		List<Order> orders = map.get(date);

		for (Order ord : orders) {
			if (ord.getOrderNumber() == orderNumber) {
				// create a deep copy of the date so any edits are not saved unless confirmed
				Order temp = new Order(ord.getOrderDate(), ord.getCustomerName(), ord.getState(), ord.getProductType(),
						ord.getArea());
				temp.setOrderNumber(ord.getOrderNumber());
				return temp;
			}
		}

		throw new OrderNotFoundException(
				"The combination of order date and order number do not match an existing order");
	}

	@Override
	public List<TaxInfo> getTaxes() {

		return taxes;
	}

	/// given an order sets up any fields in the order that have yet to be
	/// configured
	// used when creating a new order
	@Override
	public Order configureOrder(Order order) {
		// update order details
		String productType = order.getProductType();
		String orderState = order.getState();
		Product p = null;
		BigDecimal taxRate = null;

		for (Product prods : products) {
			if (prods.getProductType().equals(productType)) {
				p = prods;
				break;
			}
		}

		for (TaxInfo ti : taxes) {
			if (ti.getStateAbbreviation().equals(orderState)) {
				taxRate = ti.getTaxRate();
				break;
			}
		}

		order.setProductInfo(p);
		order.setTaxRate(taxRate);
		order.updateDetails();

		return order;
	}

	/// given an order sets up any fields in the order that have yet to be
	/// configured with supplied field values
	/// used when creating editing an order
	@Override
	public Order configureOrder(Order order, String customerName, String state, Product product, BigDecimal area) {
		// update order details

		order.setCustomerName(customerName);
		order.setState(state);
		order.setArea(area);

		BigDecimal taxRate = null;

		for (TaxInfo ti : taxes) {
			if (ti.getStateAbbreviation().equals(state)) {
				taxRate = ti.getTaxRate();
				break;
			}
		}

		order.setProductInfo(product);
		order.setTaxRate(taxRate);
		order.updateDetails();

		return order;
	}

	@Override
	public Order getOrderToDelete(LocalDate date, int orderNumber) throws OrderNotFoundException {
		List<Order> orders = map.get(date);

		for (Order ord : orders) {
			if (ord.getOrderNumber() == orderNumber) {

				return ord;
			}
		}

		throw new OrderNotFoundException(
				"The combination of order date and order number do not match an existing order");

	}

}
