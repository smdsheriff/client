package com.straders.algo.client.database.service;

import com.straders.algo.client.database.service.transaction.BrokerAccountService;
import com.straders.algo.client.database.service.transaction.ClientDetailsService;
import com.straders.algo.client.database.service.transaction.ConfigurationService;
import com.straders.algo.client.database.service.transaction.OrderService;
import com.straders.algo.client.database.service.transaction.ProfitDetailsService;
import com.straders.algo.client.database.service.transaction.SubscriptionService;
import com.straders.algo.client.database.service.transaction.UserAccountService;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ClientService {

	BrokerAccountService brokerService;

	ConfigurationService configService;

	OrderService orderService;

	SubscriptionService subscriptionService;

	UserAccountService userService;

	ClientDetailsService client;

	ProfitDetailsService profit;

}
