package com.wileyedge.flooring.comparator;

import java.util.Comparator;

import com.wileyedge.flooring.model.Order;

public class OrderNumberComparator implements Comparator<Order> {

	@Override
	public int compare(Order o1, Order o2) {

		return o1.getOrderNumber() < o2.getOrderNumber() ? -1 : 1;
	}

}
