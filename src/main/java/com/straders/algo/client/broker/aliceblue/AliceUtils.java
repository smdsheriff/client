package com.straders.algo.client.broker.aliceblue;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.straders.algo.client.broker.aliceblue.endpoint.Alice;
import com.straders.algo.client.entity.OrderEntity;

public class AliceUtils extends Alice {

	ObjectMapper mapper = new ObjectMapper();

	public String getAccessToken(OrderEntity orderEntity) {
		orderEntity.getBrokerName();
		orderEntity.getBrokerId();
		orderEntity.getPassword();
		orderEntity.getSecurityAnswer();
		// getToken();
		return "";

	}

	public Boolean checkCashPosition(OrderEntity orderEntity) {
		Integer rupees = getCapital(orderEntity);
		System.out.println("Cash Available for " + orderEntity.getUserId() + rupees);
		return rupees >= orderEntity.getCashPerTrade();
	}

	public Integer getCapital(OrderEntity orderEntity) {
		String content = getMethod(cashposition, orderEntity.getBrokerId(), orderEntity.getApiToken());
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			Map<String, Object> data = castMap(dataMap.get("data"));
			List<Map<String, Object>> cashPostions = castList(data.get("cash_positions"));
			Map<String, Object> cash = castMap(getDataMap(cashPostions).get("available"));
			Integer rupees = makeInteger(cash.get("cashmarginavailable"));
			return rupees;
		}
		return 0;
	}

	public Integer getM2M(OrderEntity orderEntity) {
		String content = getMethod(cashposition, orderEntity.getBrokerId(), orderEntity.getApiToken());
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			Map<String, Object> data = castMap(dataMap.get("data"));
			List<Map<String, Object>> cashPostions = castList(data.get("cash_positions"));
			Map<String, Object> cashMap = castMap(getDataMap(cashPostions).get("utilized"));
			Integer m2m = makeInteger(cashMap.get("realised_m2m"));
			System.out.println("Total M2M of " + orderEntity.getUserId() + m2m);
			return m2m;
		}
		return 0;
	}

	private Boolean getSuccessOrNot(Map<String, Object> dataMap) {
		if (MapUtils.isNotEmpty(dataMap)) {
			return String.valueOf(dataMap.get("status")).equalsIgnoreCase("success");
		} else {
			return false;
		}
	}

	protected String placeOrder(OrderEntity orderEntity, Map<String, String> orderMap) {
		StringEntity entity = makeEntity(orderMap);
		String content = postMethod(order, orderEntity.getBrokerId(), orderEntity.getApiToken(), entity);
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			Map<String, Object> data = castMap(dataMap.get("data"));
			String omsOrderId = String.valueOf(data.get("oms_order_id"));
			orderEntity.setOmsOrderId(omsOrderId);
			getOrder(orderEntity, omsOrderId);
			return orderEntity.isRejected() ? "REJ-" + UUID.randomUUID() : omsOrderId;
		}
		System.out.println("Place Order Response " + dataMap);
		return StringUtils.EMPTY;
	}

	protected boolean cancelOrder(OrderEntity orderEntity, String orderId) {
		String cancelUrl = cancelOrder + orderId + "&order_status=open";
		String content = deleteMethod(cancelUrl, orderEntity.getBrokerId(), orderEntity.getApiToken());
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			return true;
		}
		System.out.println("Cancel Order Response " + dataMap);
		return false;
	}

	protected boolean modifyOrder(OrderEntity orderEntity, Map<String, String> modifyMap) {
		StringEntity entity = makeEntity(modifyMap);
		String content = putMethod(order, orderEntity.getBrokerId(), orderEntity.getApiToken(), entity);
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			getOrder(orderEntity, orderEntity.getOmsOrderId());
			orderEntity.setCompleted(orderEntity.isCompleted());
			return true;
		}
		System.out.println("Modify Order Response " + dataMap);
		return false;
	}

	protected void getOrder(OrderEntity orderEntity, String orderId) {
		String getURL = order + "/" + orderId;
		String content = getMethod(getURL, orderEntity.getBrokerId(), orderEntity.getApiToken());
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			List<Map<String, Object>> dataList = castList(dataMap.get("data"));
			Map<String, Object> statusMap = getDataMap(dataList);
			String orderStatus = String.valueOf(statusMap.get("order_status"));
			System.out.println("Order Status" + orderStatus);
			if (orderStatus.equalsIgnoreCase("complete")) {
				orderEntity.setCompleted(true);
			} else if (orderStatus.equalsIgnoreCase("open") || orderStatus.equalsIgnoreCase("trigger pending")) {
				orderEntity.setOpen(true);
			} else if (orderStatus.equalsIgnoreCase("rejected")) {
				orderEntity.setRejected(true);
				orderEntity.setReason(String.valueOf(statusMap.get("rejection_reason")));
			}
		}
	}

	public Boolean getProfile(OrderEntity orderEntity) {
		String content = getMethod(profile, orderEntity.getBrokerId(), orderEntity.getApiToken());
		Map<String, Object> dataMap = getMapFromObject(content);
		if (getSuccessOrNot(dataMap)) {
			return true;
		}
		return false;
	}

}
