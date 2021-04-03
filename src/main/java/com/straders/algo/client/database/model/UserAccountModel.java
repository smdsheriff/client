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
@Table(name = "user_detail", schema = "client")
@EqualsAndHashCode
@ToString
public class UserAccountModel {

	@Id
	@Column(name = "user_id")
	public String userId;

	@Column(name = "password")
	public String password;

	@Column(name = "login_pin")
	public String loginPin;

	@Column(name = "login_token")
	public String loginToken;

	@Column(name = "contact_number")
	public String contactNumber;

	@Column(name = "email_id")
	public String emaiId;

	@Column(name = "state")
	public String state;

}
