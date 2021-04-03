package com.straders.algo.client.execute.trail;

import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.straders.algo.client.broker.aliceblue.service.AliceService;
import com.straders.algo.client.database.model.ClientDetailsModel;
import com.straders.algo.client.database.model.OrderDetailsModel;
import com.straders.algo.client.database.service.ClientService;
import com.straders.algo.client.entity.OrderEntity;
import com.straders.algo.client.enumerated.Broker;
import com.straders.algo.client.enumerated.StatusEnum;
import com.straders.algo.client.enumerated.TransactionType;
import com.straders.service.algobase.db.model.TrailOrderModel;

public class TrailOrder extends Thread {

	TrailOrderModel trailModel;

	ClientService service;

	public TrailOrder(TrailOrderModel model, ClientService clientService) {
		trailModel = model;
		service = clientService;
	}

	@Override
	public void run() {
		trailOrder();
	}

	private void trailOrder() {
		List<ClientDetailsModel> activeUserModel = service.getClient().findAll();
		activeUserModel.parallelStream().forEach(user -> {
			if (user.getSubscribed().contains(trailModel.getStrategy())) {
				trailOrder(user);
			}
		});

	}

	private void trailOrder(ClientDetailsModel user) {
		OrderEntity orderEntity = new OrderEntity();
		List<OrderDetailsModel> orderList = getOrderDetails(user);
		OrderDetailsModel executedOrder = getExecutedDetails(orderList);
		if (Objects.nonNull(executedOrder)) {
			orderEntity.setUserId(user.getUserId());
			orderEntity.setApiToken(user.getApiToken());
			orderEntity.setBrokerId(user.getBrokerId());
			orderEntity.setBrokerName(user.getBrokerName());
			orderEntity.setCashPerTrade(user.getStockRange());
			orderEntity.setSymbol(trailModel.getSymbol());
			orderEntity.setStrategy(trailModel.getStrategy());
			orderEntity.setStoplossPrice(String.valueOf(trailModel.getCurrentStoploss()));
			orderEntity.setTargetPrice(String.valueOf(trailModel.getCurrentTarget()));
			orderEntity.setStopLossDetails(getSLMDetails(orderList));
			orderEntity.setTargetDetails(getTargetDetails(orderList));
			orderEntity.setExecutedDetails(executedOrder);
			trailOrder(user, orderEntity);
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

	private OrderDetailsModel getExecutedDetails(List<OrderDetailsModel> orderList) {
		List<OrderDetailsModel> executedList = orderList.stream()
				.sorted((t1, t2) -> t2.getTime().compareTo(t1.getTime()))
				.filter(filter -> filter.getTransactionType().equalsIgnoreCase(TransactionType.MARKET.name())
						&& filter.getStatus().equalsIgnoreCase(StatusEnum.EXECUTED.name()))
				.collect(Collectors.toList());
		return executedList.stream().findFirst().isPresent() ? executedList.stream().findFirst().get() : null;
	}

	private List<OrderDetailsModel> getOrderDetails(ClientDetailsModel user) {
		return service.getOrderService().getUserOrder(new Date(Instant.now().toEpochMilli()), user.getUserId(),
				trailModel.getSymbol());
	}

	private void trailOrder(ClientDetailsModel user, OrderEntity orderEntity) {
		Broker broker = Broker.valueOf(user.getBrokerName());
		switch (broker) {
		case ALICEBLUE:
			new AliceService(service).trailOrder(user, orderEntity, trailModel);
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
