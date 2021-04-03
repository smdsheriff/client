package com.straders.algo.client.quantity;

public class Quantity {

	private Integer exposure = 8;

	private Integer valuePerStock;

	private Integer maxValuePerStock;

	public Quantity(Integer valuePerStock, Integer maxValuePerStock) {
		super();
		this.valuePerStock = valuePerStock;
		this.maxValuePerStock = maxValuePerStock;
	}

	public Integer getQuantity(Double ltp) {
		return Math.round(makeInteger(((valuePerStock / ltp) * exposure)));
	}

	public Boolean withinRange(Integer quantity, Double ltp) {
		return ((quantity * ltp) / exposure) <= (maxValuePerStock + 500);
	}

	private Integer makeInteger(Object data) {
		return (int) Math.round(Double.valueOf(String.valueOf(data)));
	}

}
