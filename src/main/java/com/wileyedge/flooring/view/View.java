package com.wileyedge.flooring.view;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import org.springframework.stereotype.Component;

import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

@Component(value = "view")
public class View implements IView {
	private Scanner sc;
	private PrintStream out;
	private String menu;

	public View() {
		sc = new Scanner(System.in);
		out = System.out;
		menu = "  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *\r\n"
				+ "  * <<Flooring Program>>\r\n" + "  * 1. Display Orders\r\n" + "  * 2. Add an Order\r\n"
				+ "  * 3. Edit an Order\r\n" + "  * 4. Remove an Order\r\n" + "  * 5. Export All Data\r\n"
				+ "  * 6. Quit\r\n" + "  *\r\n"
				+ "  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *";
	}

	// method to read an integer until valid
	private int readInt() {

		boolean flag = false; // flag to stop loop

		do {
			try {
				// get input
				String line = sc.nextLine();
				if (line.equals("")) {
					return -1;
				}
				int i = Integer.parseInt(line);

				// if invalid
				if (i < 1) {
					throw new Exception();
				} else {
					return i; // return valid input
				}

			} catch (Exception e) {
				System.out.print("  * Invalid input, please try again: ");

			}
		} while (!flag);

		return 0;
	}

	// method to read input as a date until valid
	private LocalDate readDate() {

		boolean flag = false;

		do {
			try {
				String date = sc.nextLine();
				if (date.length() != 10) {
					System.out.print("  * Invalid input,  try again: ");
					continue;
				}

				int day = Integer.parseInt(date.substring(0, 2));
				int month = Integer.parseInt(date.substring(3, 5));
				int year = Integer.parseInt(date.substring(6, 10));

				return LocalDate.of(year, month, day);
			} catch (Exception e) {
				System.out.print("  * Invalid input,  try again: ");
			}
		} while (!flag);

		return null;

	}

	//reads a decimal from user 
	private BigDecimal readBigDecimal() {

		boolean flag = false;

		do {
			try {
				BigDecimal bigDecimal = new BigDecimal(sc.nextLine()).setScale(2, RoundingMode.HALF_UP);

				return bigDecimal;
			} catch (Exception e) {
				return null;
			}
		} while (!flag);

	}

	public int printMenu() {
		out.println(menu);
		out.print("  * ");
		int i = readInt();
		while (i == -1) {
			System.out.println("  * Invalid input, please try again: ");
			out.print("  * ");
			i = readInt();
		}
		return i;
	}

	public void displayOrders(List<Order> orders) {

		out.println("  * Orders:");

		for (Order ord : orders) {
			out.println(ord.toString());
		}

	}

	public int getOrderNumber() {
		System.out.print("  * Please enter your order number:");
		int i = readInt();
		while (i == -1) {
			System.out.print("  * Invalid input, please try again: ");
			i = readInt();
		}
		return i;
	}

	@Override
	public String editName(Order order) {
		out.print("  * Enter customer name (" + order.getCustomerName() + "):");
		String newName = sc.nextLine();
		if (newName.equals("")) {
			return order.getCustomerName();
		}
		return newName;
	}

	@Override
	public String editState(Order order) {
		out.print("  * Enter customer state (" + order.getState() + "):");
		String state = sc.nextLine().toUpperCase();
		if (state.equals("")) {
			return order.getState();
		}
		return state;
	}

	@Override
	public Product editProductType(Order order, List<Product> products) {
		//out.print("  * Enter product type (" + order.getProductType() + "):");

		int counter = 1;
		for (Product p : products) {
			out.println("  * "+counter + ". " + p.toString());
			counter++;
		}

		out.print("  * Please select a product:");
		int input = readInt();
		if (input == -1) {

			for (Product p : products) {
				if (p.getProductType().equals(order.getProductType())) {
					out.println("  * Selected product: " +"\r\n  * "+p);
					return p;
				}
			}
		}

		while (input < 1 || input > counter - 1) {
			System.out.print("  * Invalid input, please try again: ");
			input = readInt();

		}
		Product selected = products.get(input - 1);
		out.println("  * Selected product: " +"\r\n  * "+ selected);

		return selected;

	}

	@Override
	public BigDecimal editArea(Order order) {
		out.print("  * Enter Area (" + order.getArea() + "):");
		BigDecimal bd = readBigDecimal();
		if (bd == null) {
			return order.getArea();
		}
		return bd;

	}

	@Override
	public LocalDate getOrderDate() {
		out.print("  * Please enter a date (dd/mm/yy): ");
		LocalDate date = readDate();
		return date;
	}

	@Override
	public String getCustomerName() {
		out.print("  * Please enter your name: ");
		String name = sc.nextLine();
		return name;
	}

	@Override
	public String getCustomerState() {
		out.print("  * Please enter your state abbreviation: ");
		String state = sc.nextLine().toUpperCase();
		return state;
	}

	@Override
	public Product displayProducts(List<Product> products) {

		int counter = 1;
		for (Product p : products) {
			out.println("  * "+counter + ". " + p.toString());
			counter++;
		}

		out.print("  * Please select a product: ");
		int input = readInt();

		while (input < 1 || input > counter - 1) {
			System.out.print("  * Invalid input, please try again: ");
			input = readInt();

		}
		Product selected = products.get(input - 1);
		out.println("  * Selected product: \r\n  * " + selected);

		return selected;
	}

	@Override
	public BigDecimal getOrderArea() {
		out.print("  * Please enter the area: ");
		return readBigDecimal();
	}

	public boolean confirmOrder(Order order) {
		out.println("  * Order details: ");
		out.println(order.showDetails());
		out.print("  * Enter Y to confirm, else enter any value to cancel: ");

		String input = sc.nextLine().toUpperCase();

		if (input.equals("Y")) {
			out.println("  * Order Confirmed");
			return true;
		} else {
			out.println("  * Order Cancelled");
			return false;
		}

	}

	public boolean confirmUpdate(Order order) {
		out.println("  * Updated Details: ");
		out.println(order.showDetails());
		out.print("  * Enter Y to confirm, else enter any value to cancel: ");

		String input = sc.nextLine().toUpperCase();

		if (input.equals("Y")) {
			out.println("  * Order Updated");
			return true;
		} else {
			out.println("  * Change Cancelled");
			return false;
		}

	}

	@Override
	public boolean confirmDelete(Order order) {
		out.println("  * Order To Be Deleted: ");
		out.println(order.showDetails());
		out.print("  * Enter Y to confirm deletion of order, else enter any key: ");
		String input = sc.nextLine().toUpperCase();
		if (input.equals("Y")) {
			out.println("  * Order Deleted");
			return true;
		}
		out.println("  * Deletion Cancelled");
		return false;
	}

	@Override
	public void displayOrderNumber(int orderNumber) {
		out.println("  * Your order number is: " + orderNumber);

	}

	@Override
	public void displayOrder(Order order) {
		out.println("  * Order Details: ");
		out.println(order);

	}

	@Override
	public void printTaxInfo(List<TaxInfo> taxes) {
		out.println("  * Supported States: ");
		for (TaxInfo ti : taxes) {
			out.println("  * "+ti.toString());
		}

	}
	// spacer used to separate sections of the application
	@Override
	public void printSpacer()
	{
		out.println("  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	}
	
}
