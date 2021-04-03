package com.straders.algo.client.database.key;

import java.io.Serializable;
import java.sql.Date;

public class ProfitDetailsKey implements Serializable {

	private static final long serialVersionUID = 1L;

	public Date date;

	public String brokerId;
}
