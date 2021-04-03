package com.straders.algo.client.database.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Immutable
@Data
@Table(name = "client_details", schema = "client")
@EqualsAndHashCode
@ToString
public class ClientDetailsModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	@Id
	public String userId;

	@Column(name = "broker_name")
	public String brokerName;

	@Column(name = "broker_id")
	public String brokerId;

	@Column(name = "api_token")
	public String apiToken;

	@Column(name = "subscribed")
	public String subscribed;

	@Column(name = "demo")
	public Boolean demo;

	@Column(name = "capital")
	public Integer capital;

	@Column(name = "stock_range")
	public Integer stockRange;

	@Column(name = "trail_range")
	public Integer trailRange;

	@Column(name = "available_capital")
	public Integer availableCapital;

}
