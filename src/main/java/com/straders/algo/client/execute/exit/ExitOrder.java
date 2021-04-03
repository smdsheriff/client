package com.straders.algo.client.execute.exit;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.straders.algo.client.broker.aliceblue.service.AliceService;
import com.straders.algo.client.database.model.ClientDetailsModel;
import com.straders.algo.client.database.model.OrderDetailsModel;
import com.straders.algo.client.database.service.ClientService;
import com.straders.algo.client.entity.OrderEntity;
import com.straders.algo.client.enumerated.Broker;
import com.straders.algo.client.enumerated.StatusEnum;
import com.straders.algo.client.enumerated.TransactionType;
import com.straders.service.algobase.db.model.ExitOrderModel;

public class ExitOrder extends Thread {

	protected ExitOrderModel exitModel;

	protected ClientService service;

	public ExitOrder(ExitOrderModel model, ClientService clientService) {
		this.exitModel = model;
		this.service = clientService;
	}

	@Override
	public void run() {
		exitOrder();
	}

	private void exitOrder() {
		List<ClientDetailsModel> activeUserModel = service.getClient().findAll();
		activeUserModel.parallelStream().forEach(user -> {
			if (user.getSubscribed().contains(exitModel.getStrategy())) {
				exitOrder(user);
			}
		});

	}

	private void exitOrder(ClientDetailsModel user) {
		OrderEntity orderEntity = new OrderEntity();
		List<OrderDetailsModel> orderList = getOrderDetails(user);
		if (checkOrderExecuted(orderList)) {
			orderEntity.setUserId(user.getUserId());
			orderEntity.setApiToken(user.getApiToken());
			orderEntity.setBrokerId(user.getBrokerId());
			orderEntity.setBrokerName(user.getBrokerName());
			orderEntity.setCashPerTrade(user.getStockRange());
			orderEntity.setStopLossDetails(getSLMDetails(orderList));
			orderEntity.setTargetDetails(getTargetDetails(orderList));
			exitOrder(user, orderEntity);
		} else {

		}
	}

	private OrderDetailsModel getTargetDetails(List<OrderDetailsModel> orderList) {
		List<OrderDetailsModel> targetList = orderList.stream()
				.filter(filter -> filter.getTransactionType().equalsIgnoreCase(TransactionType.LIMIT.name())
						&& filter.getStatus().equalsIgnoreCase(StatusEnum.PENDING.name()))
				.collect(Collectors.toList());
		return targetList.stream().findFirst().isPresent() ? targetList.stream().findFirst().get()
				: new OrderDetailsModel();
	}

	private OrderDetailsModel getSLMDetails(List<OrderDetailsModel> orderList) {
		List<OrderDetailsModel> slmList = orderList.stream()
				.filter(filter -> filter.getTransactionType().equalsIgnoreCase(TransactionType.SLM.name())
						&& filter.getStatus().equalsIgnoreCase(StatusEnum.PENDING.name()))
				.collect(Collectors.toList());
		return slmList.stream().findFirst().isPresent() ? slmList.stream().findFirst().get() : new OrderDetailsModel();
	}

	private boolean checkOrderExecuted(List<OrderDetailsModel> orderList) {
		List<OrderDetailsModel> executedList = orderList.stream()
				.filter(filter -> filter.getTransactionType().equalsIgnoreCase(TransactionType.MARKET.name())
						&& filter.getStatus().equalsIgnoreCase(StatusEnum.EXECUTED.name()))
				.collect(Collectors.toList());
		return executedList.stream().findFirst().isPresent();
	}

	private List<OrderDetailsModel> getOrderDetails(ClientDetailsModel user) {
		return service.getOrderService().getUserOrder(new Date(Instant.now().toEpochMilli()), user.getUserId(),
				exitModel.getSymbol());
	}

	private void exitOrder(ClientDetailsModel user, OrderEntity orderEntity) {
		Broker broker = Broker.valueOf(user.getBrokerName());
		switch (broker) {
		case ALICEBLUE:
			new AliceService(service).exitOrder(orderEntity, exitModel);
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
