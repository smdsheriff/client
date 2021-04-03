package com.straders.algo.client.database.model;

import java.sql.Date;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.straders.algo.client.database.key.OrderDetailsKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "order_details", schema = "client")
@IdClass(OrderDetailsKey.class)
@EqualsAndHashCode
@ToString
public class OrderDetailsModel {

	@Id
	@Column(name = "date")
	public Date date;

	@Id
	@Column(name = "symbol")
	public String symbol;

	@Id
	@Column(name = "strategy")
	public String strategy;

	@Id
	@Column(name = "order_type")
	public String orderType;

	@Id
	@Column(name = "order_id")
	public String orderId;

	@Column(name = "broker_id")
	public String brokerId;

	@Column(name = "user_id")
	public String userId;

	@Column(name = "time")
	public Time time;

	@Column(name = "broker_name")
	public String brokerName;

	@Column(name = "status")
	public String status;

	@Column(name = "transaction_type")
	public String transactionType;

	@Column(name = "price")
	public Double price;

	@Column(name = "quantity")
	public Integer quantity;
	
	@Column(name = "reason")
	public String reason;

}
