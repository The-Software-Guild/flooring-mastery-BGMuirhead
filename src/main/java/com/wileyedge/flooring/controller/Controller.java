package com.wileyedge.flooring.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wileyedge.flooring.exceptions.InvalidAreaException;
import com.wileyedge.flooring.exceptions.InvalidDateException;
import com.wileyedge.flooring.exceptions.InvalidNameException;
import com.wileyedge.flooring.exceptions.InvalidStateException;
import com.wileyedge.flooring.exceptions.NoOrdersForDateException;
import com.wileyedge.flooring.exceptions.OrderNotFoundException;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.service.IService;
import com.wileyedge.flooring.service.Service;
import com.wileyedge.flooring.view.IView;
import com.wileyedge.flooring.view.View;

public class Controller {

	public static void main(String[] args) {

		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");

		IView view = context.getBean("view", View.class);
		IService service = context.getBean("service", Service.class);

		service.initialise();

		while (true) {
			int input = view.printMenu();
			switch (input) {
			case 1: // display orders by date

				boolean case1Flag = false;

				do {
					try {
						LocalDate date = view.getOrderDate(); // get the date
						service.checkOrderDateExists(date);// check that it is a valid date
						case1Flag = true; // invalid date throws exception

						List<Order> orders = service.getOrdersByDate(date);// get the orders for the date
						view.displayOrders(orders); // display the orders for the date

					} catch (NoOrdersForDateException e1) {
						System.out.println("  * " + e1.getMessage());
						case1Flag = true;
					}
				} while (!case1Flag);

				System.out.println("  * ");
				break;

			case 2:// add an order

				boolean case2Flag = false;
				boolean validDate = false;
				boolean validName = false;
				boolean validState = false;
				boolean validArea = false;

				LocalDate newOrderDate = null;
				String newCustomerName = "";
				String newOrderState = "";
				BigDecimal area = null;
				Product selectedProduct = null;

				do {
					try {

						if (!validDate) {
							newOrderDate = view.getOrderDate();// get the order date
							validDate = service.validateOrderDate(newOrderDate);// validate the order date is in the
																				// future
							view.printSpacer();
						}

						if (!validName) {
							newCustomerName = view.getCustomerName();// get the customer name
							validName = service.validateName(newCustomerName); // validate name
							view.printSpacer();
						}

						if (!validState) {
							view.printTaxInfo(service.getTaxes());
							newOrderState = view.getCustomerState();// get the customer state
							validState = service.validateState(newOrderState); // validate state
							view.printSpacer();
						}

						if (selectedProduct == null) {
							selectedProduct = view.displayProducts(service.getProducts()); // display products to user
																							// and get selection
							view.printSpacer();
						}

						if (!validArea) {
							area = view.getOrderArea();// get the order area
							validArea = service.validateArea(area);// validate the order area
							view.printSpacer();
						}

						Order o = new Order(newOrderDate, newCustomerName, newOrderState,
								selectedProduct.getProductType(), area);

						// update order details
						o = service.configureOrder(o);

						// get user confirmation
						boolean confirm = view.confirmOrder(o);

						// user confirms the order
						if (confirm) {
							// add the order in service layer

							view.displayOrderNumber(service.addOrder(o));
							view.printSpacer();
						}
						case2Flag = true;

						// print any errors and allow user to try to submit input again
					} catch (InvalidAreaException e) {
						System.out.println("  * " + e.getMessage());
					} catch (InvalidStateException e) {
						System.out.println("  * " + e.getMessage());
					} catch (InvalidNameException e) {
						System.out.println("  * " + e.getMessage());
					} catch (InvalidDateException e) {
						System.out.println("  * " + e.getMessage());
					}
				} while (!case2Flag);
				System.out.println();
				break;

			case 3: // edit an order

				boolean case3Flag = false;
				boolean validDate2 = false;
				boolean validOrderName = false;
				boolean validOrderState = false;
				boolean validOrderArea = false;

				LocalDate orderDate = null;
				String name = "";
				BigDecimal orderArea = null;
				String state = "";

				Product prod = null;
				Order toUpdate = null;

				do {
					try {

						if (!validDate2) { // get and validate the order date
							orderDate = view.getOrderDate();
							validDate2 = service.checkOrderDateExists(orderDate);
							int orderNumber = view.getOrderNumber();
							toUpdate = service.getOrder(orderDate, orderNumber);
							view.printSpacer();
						}

						if (!validOrderName) { // get and validate name
							name = view.editName(toUpdate);
							validOrderName = service.validateName(name);
							view.printSpacer();
						}

						if (!validOrderState) { // get and validate the state
							view.printTaxInfo(service.getTaxes());
							state = view.editState(toUpdate);
							validOrderState = service.validateState(state);
							view.printSpacer();
						}

						// get product
						if (prod == null) {
							prod = view.editProductType(toUpdate, service.getProducts());
							view.printSpacer();
						}

						if (!validOrderArea) { // get and validate the area
							orderArea = view.editArea(toUpdate);
							validOrderArea = service.validateArea(orderArea);
							view.printSpacer();
						}

						toUpdate = service.configureOrder(toUpdate, name, state, prod, orderArea);

						// confirm the update
						if (view.confirmUpdate(toUpdate)) {
							// if yes then update the order
							service.editOrder(toUpdate);
							view.printSpacer();

						}

						case3Flag = true;

					} catch (NoOrdersForDateException e) {
						System.out.println("  * " + e.getMessage());
					} catch (OrderNotFoundException e) {
						validDate2 = false;
						System.out.println("  * " + e.getMessage());
					} catch (InvalidNameException e) {
						System.out.println("  * " + e.getMessage());
					} catch (InvalidStateException e) {
						System.out.println("  * " + e.getMessage());
					} catch (InvalidAreaException e) {
						System.out.println("  * " + e.getMessage());
					}
				} while (!case3Flag);
				System.out.println();
				break;

			case 4: // remove order

				boolean case4Flag = false;
				boolean orderDateDel = false;

				LocalDate orderDate3 = null;
				Order toDelete = null;

				do {
					try {

						if (!orderDateDel) { // get and validate the order date
							// get and validate the order date
							orderDate3 = view.getOrderDate();
							orderDateDel = service.validateOrderDate(orderDate3);
							// get and validate the order number
							int orderNumber = view.getOrderNumber();
							toDelete = service.getOrderToDelete(orderDate3, orderNumber);
							view.printSpacer();
						}

						// if confirm delete
						if (view.confirmDelete(toDelete)) {
							// delete the order
							service.removeOrder(toDelete);
							view.printSpacer();

						}
						case4Flag = true;

					} catch (InvalidDateException e) {
						System.out.println("  * " + e.getMessage());
					} catch (OrderNotFoundException e) {
						orderDateDel = false;
						System.out.println("  * " + e.getMessage());
					}
				} while (!case4Flag);
				System.out.println();
				break;

			case 5:
				// using service layer save the data to file
				service.exportData();
				System.out.println();
				break;

			case 6:
				System.out.println("  * " + "Thank you. Goodbye!");
				return;

			default:
				System.out.println("  * " + "Invalid input, try again\r\n  * ");
			}

		}

	}

}
