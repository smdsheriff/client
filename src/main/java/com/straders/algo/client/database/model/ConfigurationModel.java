package com.straders.algo.client.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.straders.algo.client.database.key.ConfigurationKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "configuration", schema = "client")
@IdClass(ConfigurationKey.class)
@EqualsAndHashCode
@ToString
public class ConfigurationModel {

	@Id
	@Column(name = "user_id")
	public String userId;

	@Id
	@Column(name = "broker_name")
	public String brokerName;

	@Column(name = "capital")
	public Integer capital;

	@Column(name = "per_stock")
	public Integer perStock;

	@Column(name = "trail_stock")
	public Integer trailStock;
	
	@Column(name = "max_trade")
	public Integer maxTrade;
	
	@Column(name = "max_active_trade")
	public Integer maxActiveTrade;

}
