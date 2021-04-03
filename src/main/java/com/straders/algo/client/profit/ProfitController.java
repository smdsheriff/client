package com.straders.algo.client.profit;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import com.straders.algo.client.broker.aliceblue.AliceUtils;
import com.straders.algo.client.controller.ClientController;
import com.straders.algo.client.database.model.ClientDetailsModel;
import com.straders.algo.client.database.model.ProfitDetailsModel;
import com.straders.algo.client.database.service.ClientService;
import com.straders.algo.client.entity.OrderEntity;
import com.straders.algo.client.ticker.LiveData;

@EnableScheduling
@EnableAsync
@Controller
public class ProfitController extends ClientController {

	@Scheduled(cron = "15 20 15 * * MON-FRI")
	@Async
	public void profit() {
		AliceUtils utils = new AliceUtils();
		ClientService service = clientService();
		List<ClientDetailsModel> activeUserModel = service.getClient().findAll();
		List<ProfitDetailsModel> profitModelList = new ArrayList<>();
		activeUserModel.stream().forEach(user -> {
			ProfitDetailsModel profitModel = new ProfitDetailsModel();
			OrderEntity orderEntity = new OrderEntity();
			orderEntity.setApiToken(user.getApiToken());
			orderEntity.setBrokerId(user.getBrokerId());
			profitModel.setDate(new Date(Instant.now().toEpochMilli()));
			profitModel.setBrokerId(user.getBrokerId());
			profitModel.setBrokerName(user.getBrokerName());
			profitModel.setCapital(utils.getCapital(orderEntity));
			profitModel.setProfit(utils.getM2M(orderEntity));
			profitModelList.add(profitModel);
		});
		service.getProfit().saveAll(profitModelList);
	}

//	@Scheduled(cron = "30 0/2 * * * MON-FRI")
//	@Async
	public void test() {
		LiveData data = new LiveData();
		ArrayList<Long> instrumentList = new ArrayList<Long>();
		instrumentList.add(Long.valueOf("3412"));
		try {
			data.tickerUsage(instrumentList,
					"v0kVmdnwSbM27QnkvC_Q788uJCbh3jvQYKZzNAcKKo4.8eTtXfN0APWDmKrCK4SFP0fl1E0Pb2FFdCpfFoq51UQ");
		} catch (Exception exception) {
			exception.printStackTrace();

		}
	}
}
