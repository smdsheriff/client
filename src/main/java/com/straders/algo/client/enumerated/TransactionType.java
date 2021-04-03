package com.straders.algo.client.enumerated;

import org.apache.commons.lang3.StringUtils;

public enum TransactionType {
	MARKET("MARKET"), LIMIT("LIMIT"), SLM("SL-M"), SL("SL");

	String type = StringUtils.EMPTY;

	TransactionType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
