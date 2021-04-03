package com.straders.algo.client.listener;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.straders.algo.client.controller.ClientController;
import com.straders.algo.client.execute.exit.ExitOrder;
import com.straders.algo.client.execute.place.PlaceOrder;
import com.straders.algo.client.execute.trail.TrailOrder;
import com.straders.service.algobase.db.model.ExitOrderModel;
import com.straders.service.algobase.db.model.PlaceOrderModel;
import com.straders.service.algobase.db.model.TrailOrderModel;

@Component
public class Consumer extends ClientController {

	@RabbitListener(queues = "placeOrder")
	public void placeOrder(Message orderStatus) {
		PlaceOrderModel orderEntity = (PlaceOrderModel) converter().fromMessage(orderStatus);
		new PlaceOrder(orderEntity, clientService()).start();
	}

	@RabbitListener(queues = "exitOrder")
	public void exitOrder(Message exitOrder) {
		ExitOrderModel exitModel = (ExitOrderModel) converter().fromMessage(exitOrder);
		new ExitOrder(exitModel, clientService()).start();
	}

	@RabbitListener(queues = "trailOrder")
	public void trailOrder(Message trailOrder) {
		TrailOrderModel trailModel = (TrailOrderModel) converter().fromMessage(trailOrder);
		new TrailOrder(trailModel, clientService()).start();
	}

	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}
}
