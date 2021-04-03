package com.straders.algo.client.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.straders.algo.client.database.key.BrokerAccountKey;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "broker_account", schema = "client")
@IdClass(BrokerAccountKey.class)
@EqualsAndHashCode
@ToString
public class BrokerAccountModel {

	@Id
	@Column(name = "user_id")
	public String userId;

	@Id
	@Column(name = "broker_name")
	public String brokerName;

	@Column(name = "broker_id")
	public String brokerId;

	@Column(name = "password")
	public String brokerPassword;

	@Column(name = "pin")
	public String pin;

	@Column(name = "token")
	public String token;

	@Column(name = "capital")
	public String capital;

	@Column(name = "security_answer")
	public String securityAnswer;

}
