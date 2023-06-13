package com.wileyedge.flooring.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.wileyedge.flooring.comparator.OrderNumberComparator;
import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

@Component
public class Dao implements IDao {

	private String orderHeader = null;
	private File orderFolder = new File("res\\Orders");
	private File productFile = new File("res\\Data\\Products.txt");
	private File taxesFile = new File("res\\Data\\Taxes.txt");
	private File exportFile = new File("res\\Backup\\DataExport.txt");

	private Scanner sc;
	private PrintWriter pw;

	private int maxOrderNumber = 0;

	@Override
	public int getMaxOrderNumber() {
		return maxOrderNumber;
	}
	
	/// Takes the date and a list of orders associated with the date 
	/// saves any changes made to the orders in that file to its 
	/// corresponding file
	
	@Override
	public boolean saveChanges(LocalDate date, List<Order> orders) {

		// format the date for filename
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddyyyy");
		String formattedDate = date.format(dateTimeFormatter);

		try {
			File file = new File(orderFolder.getAbsolutePath() + "\\Orders_" + formattedDate + ".txt");  // get the file

			file.createNewFile(); // create/overwrite existing file

			pw = new PrintWriter(file); // print header
			pw.write(orderHeader);

			for (Order o : orders) { // print each order formatted
				pw.write("\r\n");
				pw.write(o.formatOrder());

			}

		}// shouldnt get these errors 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			//cleanup
			pw.flush();
			pw.close();
		}

		return true;
	}
	
	//exports all orders to the backup file
	@Override
	public boolean exportOrders(Map<LocalDate, List<Order>> map) {

		// join all orders into one list
		// sort based on order ID
		// write out to backup folder + date
		Collection<List<Order>> orders = map.values();
		List<Order> allOrders = new ArrayList<>();

		for (List<Order> o1 : orders) {
			allOrders.addAll(o1);
		}

		allOrders.sort(new OrderNumberComparator());

		try {
			exportFile.createNewFile();

			pw = new PrintWriter(exportFile);
			pw.write(orderHeader + ",OrderDate");

			for (Order o2 : allOrders) {

				// format the date
				LocalDate date = o2.getOrderDate();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

				String formattedDate = date.format(formatter);
				pw.write("\r\n");
				pw.write(o2.formatOrder() + "," + formattedDate); //write the order to the file

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			pw.flush();
			pw.close();
		}

		System.out.println("  * Orders Exported Successfully");
		return true;
	}
	
	//gets all the orders from the order files
	@Override
	public Map<LocalDate, List<Order>> getOrders() {

		Map<LocalDate, List<Order>> map = new TreeMap<>();
		// orderFolder contains all order files
		// get the list of files
		String[] orderFiles = orderFolder.list();

		// for each order file
		for (String s : orderFiles) {
			// get the date to be used as the key for the map
			int orderYear = Integer.parseInt(s.substring(11, 15));
			int orderDay = Integer.parseInt(s.substring(9, 11));
			int orderMonth = Integer.parseInt(s.substring(7, 9));
			int orderNumber;
			LocalDate date = LocalDate.of(orderYear, orderMonth, orderDay);

			// open the file
			File tempFile = new File(orderFolder.getAbsolutePath() + "\\" + s);

			List<Order> orders = new ArrayList<>();
			try {
				sc = new Scanner(tempFile);
				// read the header line
				String header = sc.nextLine();
				if (orderHeader == null) {
					orderHeader = header;
				}

				// loop through all lines
				// create order obj from the data
				// add to list to return
				while (sc.hasNextLine()) {
					String[] arr = sc.nextLine().split(",");

					maxOrderNumber++;
					orderNumber = Integer.parseInt(arr[0]);
					String customerName = arr[1];
					String state = arr[2]; // is abbreviation
					BigDecimal taxRate = new BigDecimal(arr[3]);
					String productType = arr[4];
					BigDecimal area = new BigDecimal(arr[5]);
					BigDecimal costPerSquareFoot = new BigDecimal(arr[6]);
					BigDecimal labourCostPerSquareFoot = new BigDecimal(arr[7]);
					BigDecimal materialCost = new BigDecimal(arr[8]);
					BigDecimal labourCost = new BigDecimal(arr[9]);
					BigDecimal tax = new BigDecimal(arr[10]);
					BigDecimal total = new BigDecimal(arr[11]);

					Order o = new Order(date, orderNumber, customerName, state, taxRate, productType, area,
							costPerSquareFoot, labourCostPerSquareFoot, materialCost, labourCost, tax, total);

					orders.add(o);

				}
				// finished for that file
				// add to the map
				map.put(date, orders);

			} catch (FileNotFoundException fnf) {
				// TODO Auto-generated catch block
				fnf.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				sc.close();
			}

		}

		return map;
	}
	
	//get all products from products file
	
	@Override
	public List<Product> getProducts() {

		List<Product> products = new ArrayList<>();
		try {
			sc = new Scanner(productFile);
			// read the header line
			String header = sc.nextLine();

			// loop through all lines
			// create product obj from the data
			// add to list to return
			while (sc.hasNextLine()) {
				String[] arr = sc.nextLine().split(",");
				String pType = arr[0];
				BigDecimal cpsf = new BigDecimal(arr[1]);
				BigDecimal lcpsf = new BigDecimal(arr[2]);

				Product p = new Product(pType, cpsf, lcpsf);

				products.add(p);

			}

//			System.out.println("All products imported Successfully");
			return products;

		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

		return null;

	}
	
	/// gets all taxes from the tax files
	@Override
	public List<TaxInfo> getTaxInfo() {
		List<TaxInfo> taxes = new ArrayList<>();
		try {
			sc = new Scanner(taxesFile);
			// read the header line
			String header = sc.nextLine();

			// loop through all lines
			// create taxinfo obj from the data
			// add to list to return
			while (sc.hasNextLine()) {
				String[] arr = sc.nextLine().split(",");
				String sAbbr = arr[0];
				String sName = arr[1];
				BigDecimal taxRate = new BigDecimal(arr[2]);

				TaxInfo t = new TaxInfo(sAbbr, sName, taxRate);

				taxes.add(t);

			}

//			System.out.println("Tax info imported Successfully");
			return taxes;

		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}

		return null;
	}

	
	///Method only used in the Unit test to assign values for testing
		public void setTestFiles(String orderFolder, String productFile, String taxesFile, String exportFile) {
			this.orderFolder = new File(orderFolder);
			this.productFile =  new File(productFile);
			this.taxesFile =  new File(taxesFile);
			this.exportFile =  new File(exportFile);
		}


}
