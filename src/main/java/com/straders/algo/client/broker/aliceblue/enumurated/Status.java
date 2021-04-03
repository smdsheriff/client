package com.straders.algo.client.broker.aliceblue.enumurated;

public enum Status {

	SUCCESS("success"),

	REJECTED("rejected"),

	PENDING("pending");

	public String status;

	private Status(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
