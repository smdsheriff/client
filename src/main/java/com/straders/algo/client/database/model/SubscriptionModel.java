package com.straders.algo.client.database.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@Table(name = "subscription", schema = "client")
@EqualsAndHashCode
@ToString
public class SubscriptionModel {

	@Id
	@Column(name = "user_id")
	public String userId;

	@Column(name = "subscribed")
	public String subscribed;

	@Column(name = "demo")
	public Boolean demo;

}
