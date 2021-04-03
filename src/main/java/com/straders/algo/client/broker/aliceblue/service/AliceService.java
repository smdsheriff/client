package com.straders.algo.client.broker.aliceblue.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.straders.algo.client.broker.aliceblue.AliceUtils;
import com.straders.algo.client.database.model.ClientDetailsModel;
import com.straders.algo.client.database.model.OrderDetailsModel;
import com.straders.algo.client.database.service.ClientService;
import com.straders.algo.client.entity.OrderEntity;
import com.straders.algo.client.enumerated.Exchange;
import com.straders.algo.client.enumerated.OrderType;
import com.straders.algo.client.enumerated.StatusEnum;
import com.straders.algo.client.enumerated.TransactionType;
import com.straders.algo.client.quantity.Quantity;
import com.straders.service.algobase.db.model.ExitOrderModel;
import com.straders.service.algobase.db.model.TrailOrderModel;

public class AliceService extends AliceUtils {

	public enum ProductType {
		CNC, NRML, MIS, CO, BO;
	}

	protected ClientService clientService;

	public AliceService(ClientService service) {
		this.clientService = service;
	}

	public void placeOrder(OrderEntity orderEntity) {
		if (getProfile(orderEntity)) {
			if (checkCashPosition(orderEntity)) {
				List<OrderDetailsModel> orderList = new ArrayList<>();
				OrderType exitType = orderEntity.getOrderType().equalsIgnoreCase(OrderType.BUY.name()) ? OrderType.SELL
						: OrderType.BUY;
				Map<String, String> orderMap = makeMarketOrder(orderEntity);
				orderList.add(executeOrder(orderEntity, orderMap, TransactionType.MARKET));
				Map<String, String> stopLossMap = makeSLMOrder(orderEntity, exitType);
				orderEntity.setOrderType(exitType.name());
				orderList.add(executeOrder(orderEntity, stopLossMap, TransactionType.SLM));
				orderEntity.setOrderType(exitType.name());
				Map<String, String> targetMap = makeLimitOrder(orderEntity, exitType);
				orderList.add(executeOrder(orderEntity, targetMap, TransactionType.LIMIT));
				clientService.getOrderService().saveAll(orderList);
			} else {
				System.out.println(orderEntity.getUserId() + " Cash not available to trade");
			}
		} else {
			System.out.println("Profile Access Token in AliceBlue is not accessible for " + orderEntity.getUserId());
		}
	}

	private Map<String, String> makeLimitOrder(OrderEntity orderEntity, OrderType exitType) {
		return makePlaceOrder(TransactionType.LIMIT, orderEntity.getSymbol(), orderEntity.getQuantity(),
				orderEntity.getTargetPrice(), orderEntity.getStrategy(), exitType.name());
	}

	private Map<String, String> makeSLMOrder(OrderEntity orderEntity, OrderType exitType) {
		return makePlaceOrder(TransactionType.SLM, orderEntity.getSymbol(), orderEntity.getQuantity(),
				orderEntity.getStoplossPrice(), orderEntity.getStrategy(), exitType.name());
	}

	private Map<String, String> makeMarketOrder(OrderEntity orderEntity) {
		return makePlaceOrder(TransactionType.MARKET, orderEntity.getSymbol(), orderEntity.getQuantity(), "0.0",
				orderEntity.getStrategy(), orderEntity.getOrderType());
	}

	private OrderDetailsModel executeOrder(OrderEntity orderEntity, Map<String, String> orderMap,
			TransactionType transactionType) {
		String orderId = StringUtils.EMPTY;
		StatusEnum status = null;
		String priceValue = "0.0";
		switch (transactionType) {
		case LIMIT:
			priceValue = orderEntity.getTargetPrice();
			status = StatusEnum.PENDING;
			orderId = executeLimitOrder(orderEntity, orderMap);
			break;
		case MARKET:
			status = StatusEnum.EXECUTED;
			orderId = executeMarketOrder(orderEntity, orderMap);
			break;
		case SLM:
			priceValue = orderEntity.getStoplossPrice();
			status = StatusEnum.PENDING;
			orderId = executeSLMOrder(orderEntity, orderMap);
			break;
		default:
			break;
		}
		return makeOrderDetails(orderEntity, transactionType, orderId, priceValue, status);
	}

	private String executeSLMOrder(OrderEntity orderEntity, Map<String, String> orderMap) {
		return placeOrder(orderEntity, orderMap);
	}

	private String executeLimitOrder(OrderEntity orderEntity, Map<String, String> orderMap) {
		return placeOrder(orderEntity, orderMap);
	}

	private String executeMarketOrder(OrderEntity orderEntity, Map<String, String> orderMap) {
		// Boolean canPlace = checkCash ? checkCashPosition(orderEntity) : true;
		if (checkCashPosition(orderEntity)) {
			return placeOrder(orderEntity, orderMap);
		} else {
			String omsOrderId = "REJ-" + UUID.randomUUID();
			orderEntity.setOmsOrderId(omsOrderId);
			orderEntity.setReason("Cash not available");
			return omsOrderId;
		}
	}

	private Boolean executeModifyOrder(OrderEntity orderEntity, Map<String, String> orderMap) {
		return modifyOrder(orderEntity, orderMap);
	}

	private OrderDetailsModel makeOrderDetails(OrderEntity orderEntity, TransactionType transactionType, String orderId,
			String price, StatusEnum status) {
		OrderDetailsModel orderDetails = new OrderDetailsModel();
		orderDetails.setDate(getToday());
		orderDetails.setOrderType(orderEntity.getOrderType());
		orderDetails.setStrategy(orderEntity.getStrategy());
		orderDetails.setSymbol(orderEntity.getSymbol());
		orderDetails.setBrokerId(orderEntity.getBrokerId());
		orderDetails.setBrokerName(orderEntity.getBrokerName());
		orderDetails.setPrice(makeDouble(price));
		orderDetails.setQuantity(makeInteger(orderEntity.getQuantity()));
		orderDetails.setTime(getTime());
		orderDetails.setUserId(orderEntity.getUserId());
		orderDetails.setTransactionType(transactionType.name());
		orderDetails.setOrderId(orderId);
		if (orderId.contains("REJ-")) {
			System.out.println("Order not Placed for " + orderEntity.getUserId() + " for " + orderEntity.getSymbol());
			orderDetails.setReason(orderEntity.getReason());
			orderDetails.setStatus(StatusEnum.REJECTED.name());
		} else {
			orderDetails.setStatus(status.name());
		}
		return orderDetails;
	}

	private Map<String, String> makePlaceOrder(TransactionType transType, String symbol, String size, String ticks,
			String strategy, String type) {
		Map<String, String> orderMap = new HashMap<>();
		orderMap.put(exchange, Exchange.NSE.name());
		orderMap.put(orderType, transType.getType());
		orderMap.put(instrument, instrumentService.getInstrument(symbol));
		orderMap.put(quantity, String.valueOf(size));
		orderMap.put(discloseQuantity, "0");
		orderMap.put(price, String.valueOf(ticks));
		orderMap.put(transactionType, type);
		orderMap.put(triggerPrice, getTriggerPrice(transType, ticks));
		orderMap.put(validity, "DAY");
		orderMap.put(product, ProductType.MIS.name());
		orderMap.put(source, "web");
		orderMap.put(orderTag, strategy);
		return orderMap;
	}

	private Map<String, String> makeCancelOrder(OrderDetailsModel details) {
		Map<String, String> orderMap = new HashMap<>();
		orderMap.put(orderId, details.getOrderId());
		orderMap.put(exchange, Exchange.NSE.name());
		orderMap.put(orderType, details.getTransactionType());
		orderMap.put(instrument, instrumentService.getInstrument(details.getSymbol()));
		orderMap.put(quantity, String.valueOf(quantity));
		orderMap.put(discloseQuantity, "0");
		orderMap.put(price, String.valueOf(price));
		orderMap.put(transactionType, details.getOrderType());
		orderMap.put(triggerPrice, String.valueOf(details.getPrice()));
		orderMap.put(validity, "DAY");
		orderMap.put(product, ProductType.MIS.name());
		return orderMap;
	}

	private String getTriggerPrice(TransactionType orderType, String price) {
		switch (orderType) {
		case LIMIT:
		case SLM:
			return price;
		case MARKET:
			return "0.0";
		default:
			return "0.0";
		}
	}

	public void exitOrder(OrderEntity orderEntity, ExitOrderModel exitModel) {
		if (getProfile(orderEntity)) {
			List<OrderDetailsModel> ordersList = new ArrayList<>();
			if (exitModel.getType().equalsIgnoreCase("TARGET")) {
				OrderDetailsModel stopLossDetails = orderEntity.getStopLossDetails();
				stopLossDetails.setStatus(StatusEnum.CANCELLED.name());
				executeCancelOrder(orderEntity, stopLossDetails);
				OrderDetailsModel targetDetails = orderEntity.getTargetDetails();
				targetDetails.setStatus(StatusEnum.EXECUTED.name());
				executedOrder(orderEntity, targetDetails);
				ordersList.add(stopLossDetails);
				ordersList.add(targetDetails);
				clientService.getOrderService().saveAll(ordersList);
			} else if (exitModel.getType().equalsIgnoreCase("STOPLOSS")) {
				OrderDetailsModel stopLossDetails = orderEntity.getStopLossDetails();
				stopLossDetails.setStatus(StatusEnum.EXECUTED.name());
				executedOrder(orderEntity, stopLossDetails);
				OrderDetailsModel targetDetails = orderEntity.getTargetDetails();
				targetDetails.setStatus(StatusEnum.CANCELLED.name());
				executeCancelOrder(orderEntity, targetDetails);
				ordersList.add(stopLossDetails);
				ordersList.add(targetDetails);
				clientService.getOrderService().saveAll(ordersList);
			}
		} else {
			System.out.println(
					"Profile Access Token in AliceBlue is not accessible for Exit Order" + orderEntity.getUserId());
		}
	}

	private void executedOrder(OrderEntity orderEntity, OrderDetailsModel orderDetails) {
		getOrder(orderEntity, orderDetails.getOrderId());
		if (orderEntity.isCompleted()) {
			System.out.println("Order executed successfully " + orderDetails.getOrderId());
		} else if (orderEntity.isOpen()) {
			Map<String, String> modifyMap = makeModifyOrder(TransactionType.MARKET, orderDetails.getSymbol(),
					orderDetails.getQuantity().toString(), "0.0", orderDetails.getOrderType(), orderDetails);
			orderEntity.setOmsOrderId(orderDetails.getOrderId());
			Boolean modifiedOrder = executeModifyOrder(orderEntity, modifyMap);
			System.out.println(
					"Order Modified for " + orderDetails.getOrderId() + (modifiedOrder && orderEntity.isCompleted()));
		}
	}

	private void executeCancelOrder(OrderEntity orderEntity, OrderDetailsModel orderDetails) {
		getOrder(orderEntity, orderDetails.getOrderId());
		if (orderEntity.isOpen()) {
			cancelOrder(orderEntity, orderDetails.getOrderId());
		} else if (orderEntity.isCompleted()) {
			OrderType exitType = orderEntity.getOrderType().equalsIgnoreCase(OrderType.BUY.name()) ? OrderType.SELL
					: OrderType.BUY;
			orderEntity.setSymbol(orderDetails.getSymbol());
			orderEntity.setQuantity(orderDetails.getQuantity().toString());
			orderEntity.setOrderType(exitType.name());
			orderEntity.setStrategy(orderDetails.getStrategy());
			Map<String, String> orderMap = makeMarketOrder(orderEntity);
			executeMarketOrder(orderEntity, orderMap);
		}
	}

	public void trailOrder(ClientDetailsModel user, OrderEntity orderEntity, TrailOrderModel trailModel) {
		if (getProfile(orderEntity)) {
			List<OrderDetailsModel> orderList = new ArrayList<>();
			OrderDetailsModel executedDetails = orderEntity.getExecutedDetails();
			OrderDetailsModel stopLossDetails = orderEntity.getStopLossDetails();
			OrderDetailsModel targetDetails = orderEntity.getTargetDetails();
			String increasedQuantity = increaseQuantity(user, trailModel, executedDetails.getQuantity());
			System.out.println("Actual Increased quantity" + increasedQuantity);
			orderEntity.setQuantity(increasedQuantity);
			orderEntity.setOrderType(executedDetails.getOrderType());
			Map<String, String> modifyMap = makeModifyOrder(TransactionType.MARKET, executedDetails.getSymbol(),
					increasedQuantity, "0.0", executedDetails.getOrderType(), stopLossDetails);
			orderList.add(executeModifyOrder(orderEntity, modifyMap, order, TransactionType.MARKET, "0.0",
					StatusEnum.EXECUTED, executedDetails.getOrderType(), stopLossDetails));
			targetDetails.setStatus(StatusEnum.EXECUTED.name());
			orderList.add(targetDetails);
			if (orderEntity.isCompleted()) {
				OrderType exitType = targetDetails.getOrderType().equalsIgnoreCase(OrderType.BUY.name()) ? OrderType.BUY
						: OrderType.SELL;
				orderEntity.setStoplossPrice(trailModel.getCurrentStoploss().toString());
				orderEntity.setTargetPrice(trailModel.getCurrentTarget().toString());
				orderEntity.setOrderType(exitType.name());
				Map<String, String> stopLossMap = makeSLMOrder(orderEntity, exitType);
				orderList.add(executeOrder(orderEntity, stopLossMap, TransactionType.SLM));
				Map<String, String> targetMap = makeLimitOrder(orderEntity, exitType);
				orderList.add(executeOrder(orderEntity, targetMap, TransactionType.LIMIT));
			} else {
				System.out.println(
						"Trail Order not executed for " + user.getUserId() + " for " + orderEntity.getSymbol());
			}
			clientService.getOrderService().saveAll(orderList);
		} else {
			System.out.println("Profile Access Token in AliceBlue is not accessible for " + orderEntity.getUserId());
		}
	}

	private String increaseQuantity(ClientDetailsModel user, TrailOrderModel trailModel, Integer quantity) {
		return String.valueOf(quantity + makeQuantity(user, trailModel));

	}

	private Integer makeQuantity(ClientDetailsModel client, TrailOrderModel trailModel) {
		Quantity quantity = new Quantity(client.getStockRange(), client.getTrailRange());
		Integer increasedQuantity = quantity.getQuantity(trailModel.getCurrentPrice());
		return quantity.withinRange(increasedQuantity, trailModel.getCurrentPrice()) ? increasedQuantity : 0;
	}

	private Map<String, String> makeModifyOrder(TransactionType transType, String symbol, String increasedQuantity,
			String modifiedPrice, String type, OrderDetailsModel orderDetails) {
		Map<String, String> orderMap = new HashMap<>();
		orderMap.put(exchange, Exchange.NSE.name());
		orderMap.put(orderType, transType.getType());
		orderMap.put(orderId, orderDetails.getOrderId());
		orderMap.put(instrument, instrumentService.getInstrument(symbol));
		orderMap.put(quantity, String.valueOf(increasedQuantity));
		orderMap.put(discloseQuantity, "0");
		orderMap.put(price, String.valueOf(modifiedPrice));
		orderMap.put(transactionType, type);
		orderMap.put(triggerPrice, getTriggerPrice(transType, modifiedPrice));
		orderMap.put(validity, "DAY");
		orderMap.put(product, ProductType.MIS.name());
		orderMap.put(source, "web");
		return orderMap;
	}

	private OrderDetailsModel executeModifyOrder(OrderEntity orderEntity, Map<String, String> modifyMap,
			String orderUrl, TransactionType transactionType, String price, StatusEnum status, String orderType,
			OrderDetailsModel orderDetails) {
		orderEntity.setOmsOrderId(orderDetails.getOrderId());
		Boolean isModified = executeModifyOrder(orderEntity, modifyMap);
		if (isModified && orderEntity.isCompleted()) {
			orderDetails.setStatus(status.name());
		} else {
			System.out.println("Order not Placed for " + orderEntity.getUserId() + " for " + orderEntity.getSymbol());
			orderDetails.setReason(orderEntity.getReason());
			orderDetails.setStatus(StatusEnum.REJECTED.name());
		}
		return orderDetails;
	}
}
