package com.straders.algo.client.database.model;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.straders.algo.client.database.key.BrokerAccountKey;
import com.straders.algo.client.database.key.ProfitDetailsKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "profit_details", schema = "client")
@IdClass(ProfitDetailsKey.class)
@EqualsAndHashCode
@ToString
public class ProfitDetailsModel {

	@Id
	@Column(name = "date")
	public Date date;

	@Id
	@Column(name = "broker_id")
	public String brokerId;

	@Column(name = "broker_name")
	public String brokerName;

	@Column(name = "capital")
	public Integer capital;

	@Column(name = "profit")
	public Integer profit;

}
