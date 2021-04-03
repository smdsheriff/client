package com.straders.algo.client.execute.place;

import java.util.List;

import com.straders.algo.client.broker.aliceblue.service.AliceService;
import com.straders.algo.client.database.model.ClientDetailsModel;
import com.straders.algo.client.database.service.ClientService;
import com.straders.algo.client.entity.OrderEntity;
import com.straders.algo.client.enumerated.Broker;
import com.straders.algo.client.quantity.Quantity;
import com.straders.service.algobase.db.model.PlaceOrderModel;

public class PlaceOrder extends Thread {

	protected PlaceOrderModel orderModel;

	protected ClientService service;

	public PlaceOrder(PlaceOrderModel model, ClientService clientService) {
		this.orderModel = model;
		this.service = clientService;
	}

	@Override
	public void run() {
		placeOrder();
	}

	private void placeOrder() {
		List<ClientDetailsModel> activeUserModel = service.getClient().findAll();
		activeUserModel.parallelStream().forEach(user -> {
			if (user.getSubscribed().contains(orderModel.getStrategy())) {
				placeOrder(user);
			}
		});
	}

	private void placeOrder(ClientDetailsModel client) {
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setUserId(client.getUserId());
		orderEntity.setApiToken(client.getApiToken());
		orderEntity.setBrokerId(client.getBrokerId());
		orderEntity.setBrokerName(client.getBrokerName());
		orderEntity.setCashPerTrade(client.getStockRange());
		orderEntity.setQuantity(makeQuantity(client));
		orderEntity.setStrategy(orderModel.getStrategy());
		orderEntity.setSymbol(orderModel.getSymbol());
		orderEntity.setOrderType(orderModel.getOrderType());
		orderEntity.setStrikePrice(orderModel.getStrikePrice().toString());
		orderEntity.setTargetPrice(orderModel.getTargetPrice().toString());
		orderEntity.setStoplossPrice(orderModel.getStoplossPrice().toString());
		placeOrder(client, orderEntity);
	}

	private String makeQuantity(ClientDetailsModel client) {
		Quantity quantity = new Quantity(client.getStockRange(), client.getTrailRange());
		String quants = String.valueOf((quantity.getQuantity(orderModel.getStrikePrice())));
		System.out.println("Actual Quantity" + quants);
		return quants;
	}

	private void placeOrder(ClientDetailsModel user, OrderEntity orderEntity) {
		Broker broker = Broker.valueOf(user.getBrokerName());
		switch (broker) {
		case ALICEBLUE:
			new AliceService(service).placeOrder(orderEntity);
			break;
		case FYERS:
			break;
		case UPSTOX:
			break;
		case ZERODHA:
			break;
		default:
			break;
		}
	}

}
