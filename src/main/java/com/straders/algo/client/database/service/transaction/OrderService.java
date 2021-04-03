package com.straders.algo.client.database.service.transaction;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.model.OrderDetailsModel;
import com.straders.algo.client.database.repository.OrderDetailsRepository;

@Service
public class OrderService {

	@Autowired
	public OrderDetailsRepository order;

	public List<OrderDetailsModel> findAll() {
		return order.findAll();
	}

	public List<OrderDetailsModel> getUserOrder(Date date, String userId, String symbol) {
		return order.getUserOrder(date, userId, symbol);
	}

	public void save(OrderDetailsModel model) {
		order.save(model);
	}

	public void saveAll(List<OrderDetailsModel> modelList) {
		order.saveAll(modelList);
	}
}
