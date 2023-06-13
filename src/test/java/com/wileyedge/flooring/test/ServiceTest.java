package com.wileyedge.flooring.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import com.wileyedge.flooring.service.Service;

public class ServiceTest {
	Service service;

	// No test for methods that call dao methods directly as they have separate
	// testing

	@Before
	public void setUp() throws Exception {
		String orderFolder = "res\\Test\\Orders";
		String productFile = "res\\Test\\Data\\Products.txt";
		String taxesFile = "res\\Test\\Data\\Taxes.txt";
		String exportFile = "res\\Test\\Backup\\DataExport.txt";

		// manual dao setup for testing
		Dao dao = new Dao();
		dao.setTestFiles(orderFolder, productFile, taxesFile, exportFile);
		service = new Service(dao);
		service.initialise(); // needs to be set up from begining

	}

	@After
	public void tearDown() throws Exception {
		service = null;
	}

	@Test()
	public void testInitialiseAndGetter() {

		// if initialiser failed these would be null

		// taxes and products should be read in
		List<TaxInfo> taxes = service.getTaxes();
		assertNotEquals(taxes.size(), 0);

		List<Product> products = service.getProducts();
		assertNotEquals(products.size(), 0);

	}

	@Test()
	public void testGetOrdersByDate() {

		// date of first order
		LocalDate date = LocalDate.of(2013, 6, 1);

		// get the orders for that date
		List<Order> orders = service.getOrdersByDate(date);

		// is the date of the first(and only) order the same as expected
		assertEquals(orders.get(0).getOrderDate(), date);

	}

	@Test
	public void testAddAndRemoveOrder() {

		// new order date
		LocalDate date = LocalDate.of(2013, 6, 2);

		// test order
		Order testOrder = new Order(date, "Test Order", "CA", "Carpet", new BigDecimal("100.00"));
		testOrder.setProductInfo(new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10")));
		testOrder.setTaxRate(new BigDecimal("4.45"));
		testOrder.updateDetails();
		testOrder.setOrderNumber(2);
		service.addOrder(testOrder);

		// get the orders for the date we just added
		List<Order> orders = service.getOrdersByDate(date);
		// the order should have been added and so should have an order in it
		assertNotNull(orders);
		Order order = orders.get(0);

		assertEquals(date, order.getOrderDate());
		assertEquals("CA", order.getState());
		assertEquals("Carpet", order.getProductType());
		assertEquals(new BigDecimal("100.00"), order.getArea());

		// remove the order
		service.removeOrder(testOrder);
		orders = service.getOrdersByDate(date);
		// last order should be removed so should be size 0
		assertEquals(orders.size(), 0);

	}

	@Test
	public void testEditOrder() {

		// edit the order

		// date of first order
		// new order date
		LocalDate date = LocalDate.of(2013, 6, 2);

		// test order
		Order testOrder = new Order(date, "Test Order", "CA", "Carpet", new BigDecimal("100.00"));
		testOrder.setProductInfo(new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10")));
		testOrder.setTaxRate(new BigDecimal("4.45"));
		testOrder.updateDetails();
		testOrder.setOrderNumber(2);
		service.addOrder(testOrder);

		testOrder.setCustomerName("Name Change");
		service.editOrder(testOrder);

		// get the order that should have been updated
		List<Order> orders = service.getOrdersByDate(date);
		Order order = orders.get(0);

		assertEquals(order.getCustomerName(), "Name Change");

		// delete the added order
		service.removeOrder(testOrder);
		orders = service.getOrdersByDate(date);
		// last order should be removed so should be size 0
		assertEquals(orders.size(), 0);
	}

	@Test
	public void testCheckOrderDateExists() {

		// date with order
		LocalDate date1 = LocalDate.of(2013, 6, 1);

		// date without order
		LocalDate date2 = LocalDate.of(3000, 6, 1);

		try {
			assertTrue(service.checkOrderDateExists(date1));
		} catch (NoOrdersForDateException e1) {
			assertTrue(false);
		}

		try {
			assertFalse(service.checkOrderDateExists(date2));
		} catch (NoOrdersForDateException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testValidateOrderDate() {

		// date that should pass
		LocalDate date1 = LocalDate.of(2025, 6, 1);

		// date that shouldnt pass
		LocalDate date2 = LocalDate.of(2000, 6, 1);

		try {
			assertTrue(service.validateOrderDate(date1));
		} catch (InvalidDateException e) {
			assertTrue(false);
		}

		try {
			assertFalse(service.validateOrderDate(date2));
		} catch (InvalidDateException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testValidateName() {

		// name that should pass
		String name1 = "Test Name, Inc.";
		// name that shouldnt pass
		String name2 = "Test Name!!";
		// name that shouldnt pass
		String name3 = "";

		try {
			assertTrue(service.validateName(name1));
		} catch (InvalidNameException e) {
			assertTrue(false);
		}

		try {
			assertFalse(service.validateName(name2));
		} catch (InvalidNameException e) {
			assertTrue(true);
		}

		try {
			assertFalse(service.validateName(name3));
		} catch (InvalidNameException e) {
			assertTrue(true);

		}

	}

	@Test
	public void testValidateState() {

		// only TX is in the taxes file so should be the only input that passes
		String state1 = "TX";
		String state2 = "";
		String state3 = "Texas";
		String state4 = "Ca";
		String state5 = "CA";

		try {
			assertTrue(service.validateState(state1));
		} catch (InvalidStateException e) {
			assertTrue(false);
		}

		try {
			assertFalse(service.validateState(state2));
		} catch (InvalidStateException e) {
			assertTrue(true);
		}
		try {
			assertFalse(service.validateState(state3));
		} catch (InvalidStateException e) {
			assertTrue(true);
		}
		try {
			assertFalse(service.validateState(state4));
		} catch (InvalidStateException e) {
			assertTrue(true);
		}
		try {
			assertFalse(service.validateState(state5));
		} catch (InvalidStateException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testValidateArea() {
		// only <100.00 should be accepted

		// should pass
		BigDecimal area1 = new BigDecimal("100");
		BigDecimal area2 = new BigDecimal("99.999").setScale(2, RoundingMode.HALF_UP);
		// should fail
		BigDecimal area3 = new BigDecimal("99.99");
		BigDecimal area4 = new BigDecimal("5");

		try {
			assertTrue(service.validateArea(area1));
		} catch (InvalidAreaException e1) {
			assertTrue(false);
		}

		try {
			assertTrue(service.validateArea(area2));
		} catch (InvalidAreaException e1) {
			assertTrue(false);
		}

		try {
			assertFalse(service.validateArea(area3));
		} catch (InvalidAreaException e) {
			assertTrue(true);
		}

		try {
			assertFalse(service.validateArea(area4));
		} catch (InvalidAreaException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testGetOrder() {
		// date of first order
		LocalDate date = LocalDate.of(2013, 6, 1);
		//there is only an order number 1 here
		
		Order order = null;
		try {
			order = service.getOrder(date, 1);
			assertNotNull(order);
		} catch (OrderNotFoundException e) {
			assertTrue(false);
		}
		
		
		
		try {
			order = service.getOrder(date, 2);
			assertNull(order);
		} catch (OrderNotFoundException e) {
			assertTrue(true);
		}
		
		
		
		
		
		
		
	}
	
	public void testGetOrderToDelete() {
		// date of first order
		LocalDate date = LocalDate.of(2013, 6, 1);
		//there is only an order number 1 here
		
		Order order = null;
		try {
			order = service.getOrder(date, 1);
			assertNotNull(order);
		} catch (OrderNotFoundException e) {
			assertTrue(false);
		}
		
		
		
		try {
			order = service.getOrder(date, 2);
			assertNull(order);
		} catch (OrderNotFoundException e) {
			assertTrue(true);
		}
		
		
		
		
		
		
		
	}

	public void testConfigureOrder()
	{
		LocalDate date = LocalDate.of(2025, 6, 1);
		
		//first method
		Order testOrder = new Order(date, "Test Order", "TX", "Carpet", new BigDecimal("100.00"));

		
		Order temp1 = service.configureOrder(testOrder);
		
		
		//check output string matches expected values;
		String expected = "0,Test Customer,TX,4.45,Carpet,100.00,2.25,2.10,225.00,210.00,19.36,454.36,06-01-2025";
		String actual = temp1.toString();
		
		assertEquals(expected, actual);
		
		
		
		
		//second method
		
		Order testOrder2 = new Order(date, "Test Order", "TX", "Carpet", new BigDecimal("100.00"));
		testOrder.setProductInfo(new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10")));
		testOrder.setTaxRate(new BigDecimal("4.45"));
		testOrder.updateDetails();
		testOrder.setOrderNumber(2);
		
		Product p = new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10"));
		
		Order temp2 = service.configureOrder(testOrder2, "Name", "TX", p, new BigDecimal("200"));
		
		//second method should update the order information
		
		assertEquals(temp2.getCustomerName(), "Name");
		expected = "0,Name,TX,4.45,Carpet,100.00,2.25,2.10,225.00,210.00,19.36,454.36,06-01-2025";
		actual = temp1.toString();
		
		assertEquals(expected, actual);
		
		
	}

}
