package com.straders.algo.client.entity;

import com.straders.algo.client.database.model.OrderDetailsModel;

import lombok.Data;

@Data
public class OrderEntity {

	public String userId;

	public String password;

	public String apiToken;

	public String symbol;

	private String strategy;

	private String orderType;

	private String transactionType;

	private String strikePrice;

	private String targetPrice;

	private String stoplossPrice;

	private String quantity;

	private String brokerId;

	private String brokerName;

	private Integer cashPerTrade;

	private String securityAnswer;

	private String reason;

	private String omsOrderId;

	private Integer maxTrade;

	private boolean isOpen;

	private boolean isRejected;

	private boolean isCompleted;

	private OrderDetailsModel stopLossDetails;

	private OrderDetailsModel targetDetails;

	private OrderDetailsModel executedDetails;

}
