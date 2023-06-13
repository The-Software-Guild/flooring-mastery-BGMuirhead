package com.wileyedge.flooring.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.wileyedge.flooring.model.Order;
import com.wileyedge.flooring.model.Product;
import com.wileyedge.flooring.model.TaxInfo;

//@Component(value = "dao")
public interface IDao {
	
	
	boolean saveChanges(LocalDate date ,List<Order> orders);
	Map<LocalDate,List<Order>> getOrders();
	List<Product> getProducts();
	List<TaxInfo> getTaxInfo();
	int getMaxOrderNumber();
	boolean exportOrders(Map<LocalDate,List<Order>> map);

}
