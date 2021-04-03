package com.straders.algo.client.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.straders.algo.client.database.service.ClientService;
import com.straders.algo.client.database.service.transaction.BrokerAccountService;
import com.straders.algo.client.database.service.transaction.ClientDetailsService;
import com.straders.algo.client.database.service.transaction.ConfigurationService;
import com.straders.algo.client.database.service.transaction.OrderService;
import com.straders.algo.client.database.service.transaction.ProfitDetailsService;
import com.straders.algo.client.database.service.transaction.SubscriptionService;
import com.straders.algo.client.database.service.transaction.UserAccountService;

public abstract class ClientController {

	@Autowired
	BrokerAccountService brokerService;

	@Autowired
	ConfigurationService configService;

	@Autowired
	OrderService orderService;

	@Autowired
	SubscriptionService subscriptionService;

	@Autowired
	UserAccountService userService;

	@Autowired
	ClientDetailsService client;

	@Autowired
	ProfitDetailsService profit;

	public ClientService clientService() {
		ClientService service = new ClientService();
		service.setBrokerService(brokerService);
		service.setConfigService(configService);
		service.setOrderService(orderService);
		service.setSubscriptionService(subscriptionService);
		service.setUserService(userService);
		service.setClient(client);
		service.setProfit(profit);
		return service;
	}

}
