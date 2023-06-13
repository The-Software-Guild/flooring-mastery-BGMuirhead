package com.wileyedge.flooring.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wileyedge.flooring.dao.Dao;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

public class DaoTest {

	Dao dao;
	Map<LocalDate, List<Order>> map;

	@Before
	public void setUp() throws Exception {
		String orderFolder = "res\\Test\\Orders";
		String productFile = "res\\Test\\Data\\Products.txt";
		String taxesFile = "res\\Test\\Data\\Taxes.txt";
		String exportFile = "res\\Test\\Backup\\DataExport.txt";

		// set up the locaton of the test files
		dao = new Dao();
		dao.setTestFiles(orderFolder, productFile, taxesFile, exportFile);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetOrders() {
		// initially should load only one order
		// max order number should be 0 then 1
		testGetMaxOrderNumber(0);

		map = dao.getOrders();

		testGetMaxOrderNumber(1);
		// should only be one key
		Set<LocalDate> dates = map.keySet();

		assertEquals(dates.size(), 2);

		// there should also only be one order
		List<Order> orders = map.get(dates.iterator().next());

		assertEquals(orders.size(), 1);

	}

	public void testGetMaxOrderNumber(int expected) {
		// having read one order in max order number should be 1;
		int orderNumber = dao.getMaxOrderNumber();
		assertEquals(orderNumber, expected);

	}

	@Test
	public void testGetTaxes() {
		List<TaxInfo> taxes = dao.getTaxInfo();

		// Only one Tax location in the test taxes file
		assertEquals(taxes.size(), 1);
		TaxInfo ti = taxes.get(0);
		// Tax info should be TX,Texas,4.45

		assertEquals(ti.getStateAbbreviation(), "TX");
		assertEquals(ti.getStateName(), "Texas");
		assertEquals(ti.getTaxRate(), new BigDecimal("4.45"));

	}

	@Test
	public void testGetProducts() {
		List<Product> prod = dao.getProducts();

		// Only one product is in the test products file
		assertEquals(prod.size(), 1);
		Product p = prod.get(0);
		// product should be Carpet,2.25,2.10
		assertEquals(p.getProductType(), "Carpet");
		assertEquals(p.getCostPerSquareFoot(), new BigDecimal("2.25"));
		assertEquals(p.getLabourCostPerSquareFoot(), new BigDecimal("2.10"));

	}

	@Test
	public void testSaveAndRemove() {
		// -- edit existing --//

		// read the file
		map = dao.getOrders();

		// date of existing entry
		LocalDate date = map.keySet().iterator().next();

		// -- make a new order --//
		// create a new order
		Order testOrder = new Order(date, "Test Customer 2", "TX", "Carpet", new BigDecimal("100.00"));
		testOrder.setProductInfo(new Product("Carpet", new BigDecimal("2.25"), new BigDecimal("2.10")));
		testOrder.setTaxRate(new BigDecimal("4.45"));
		testOrder.updateDetails();
		testOrder.setOrderNumber(2);
		// add the order to existing orders

		List<Order> orders = map.get(date);
		orders.add(testOrder);
		map.put(date, orders);
		// save the changes
		dao.saveChanges(date, map.get(date));
		// read the file again
		Map<LocalDate, List<Order>> tempMap = dao.getOrders();

		orders = tempMap.get(date);
		// size should be 2 now
		assertEquals(orders.size(), 2);
		// get the second order
		Order order = orders.get(1);

		assertEquals(order.getCustomerName(), "Test Customer 2");

		// cleanup - remove the added entry

		orders = map.get(date);
		orders.remove(testOrder);
		map.put(date, orders);
		// save the changes
		dao.saveChanges(date, map.get(date));

		// read the file again
		tempMap = dao.getOrders();

		orders = tempMap.get(date);
		// size should be 1 again
		assertEquals(orders.size(), 1);

	}

	@Test
	public void testExportOrders() {

		// read the file
		map = dao.getOrders();
		
		dao.exportOrders(map);
		
		try {
			//get data export file 
			Scanner sc = new Scanner(new File("res\\Test\\Backup\\DataExport.txt"));
			//first line should be header
			String line = sc.nextLine();
			String header = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total,OrderDate";
			assertEquals(header, line);
			//next line should be the same as the only order with date appended
			line = "1,Ada Lovelace,CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06,06-01-2013";
			String exportedOrder = sc.nextLine();
			assertEquals(exportedOrder, line);
		
		
		
		
		
		
		
		
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

}
