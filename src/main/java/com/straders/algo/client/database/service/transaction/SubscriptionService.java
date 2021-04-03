package com.straders.algo.client.database.service.transaction;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.model.SubscriptionModel;
import com.straders.algo.client.database.repository.SubscriptionRepository;
import com.straders.service.algobase.db.keys.ExitOrderKey;

@Service
public class SubscriptionService {

	@Autowired
	public SubscriptionRepository sub;

	public void save(SubscriptionModel model) {
		sub.save(model);
	}

	public void saveAll(List<SubscriptionModel> exitList) {
		sub.saveAll(exitList);
	}

	public boolean existsById(String keys) {
		return sub.existsById(keys);
	}

}
