package com.straders.algo.client.template;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.neovisionaries.ws.client.WebSocket;
import com.straders.algo.client.broker.aliceblue.AliceUtils;
import com.straders.algo.client.broker.aliceblue.endpoint.Alice;
import com.straders.algo.client.database.model.OrderDetailsModel;
import com.straders.algo.client.entity.OrderEntity;
import com.straders.algo.client.ticker.LiveData;
import com.straders.algo.client.ticker.operation.OnConnect;

import ch.qos.logback.core.net.SyslogOutputStream;

public class TestUrils {

	public static void main(String[] args)
			throws UnsupportedEncodingException, JsonMappingException, JsonProcessingException {
		AliceUtils utils = new AliceUtils();
		String instrumentToken = utils.instrumentService.getInstrument("ZEEL");
		LiveData data = new LiveData();
		ArrayList<Long> instrumentList = new ArrayList<Long>();
		instrumentList.add(Long.valueOf(instrumentToken));
		try {
			//4QrihwDISadgAHlum7KJs2QkHKdmI3JvkNiK94kvnkw.32GDeHeojABawBZcMzySEU67Pfy6Jk3IP6ZYA4yisTY
				System.out.println(" Check");
				data.tickerUsage(instrumentList,
						"SwIXG1m9BE9QyMTXbJYuD207YTJ1RWhl-gN32iQTzHM.aKg5HwYTvuc7yE141_iYHN1cNNpS_813_MsCp2B6pKA");
		} catch (Exception exception) {
			exception.printStackTrace();

		}
		// String content = template.deleteMethod(
		// "https://ant.aliceblueonline.com/api/v2/order?oms_order_id=210220000003674&order_status=open",
		// "AB106595",
		// "_DsBbBX44-l_dCu-ah-lkA00rm905M-_Y71BZpQcHCw.NqFM7-sQbHCPjX3qg-IXZr5atkzJtmVECtB8OFEp224");
		// System.out.println(content);
		// ObjectMapper mapper = new ObjectMapper();
		// Map<String, Object> dataMap =
		// mapper.readValue(String.valueOf(content),
		// new TypeReference<Map<String, Object>>() {
		// });
		// boolean sucess =
		// String.valueOf(dataMap.get("status")).equalsIgnoreCase("success");
		// if (sucess) {
		// List<Map<String, Object>> cashPostions = (List<Map<String, Object>>)
		// dataMap.get("data");
		// System.out.println(cashPostions.stream().findFirst().get().get("order_status"));
		// cashPostions.stream().findFirst().get().get("rejection_reason");
		// }
		// OrderEntity orderEntity = new OrderEntity();
		// orderEntity.setBrokerId("AB106595");
		// orderEntity
		// .setApiToken("DraLXXrpvX3Y_AnxFylkI6Y4KJpNg6C9QY-4dbD2gBo.aHKtL0yQ8-esCBd5U8tA6R7wuvaAZrL-WVJvIszCAE0");
		// System.out.println(utils.getM2M(orderEntity));
		// double t = 161.5854;
		// toDecimal(t);
	}

	public static void toDecimal(double data) {
		Double number = Math.ceil(data / 0.05) * 0.05;
		DecimalFormat df = new DecimalFormat("#.##");
		Double.valueOf(df.format(number));
	}

}
