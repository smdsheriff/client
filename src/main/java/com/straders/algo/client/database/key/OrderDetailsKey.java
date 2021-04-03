package com.straders.algo.client.database.key;

import java.io.Serializable;
import java.sql.Date;

public class OrderDetailsKey implements Serializable {

	private static final long serialVersionUID = 1L;

	public Date date;

	public String orderId;

	public String symbol;

	public String orderType;
	
	public String strategy;
	


}
