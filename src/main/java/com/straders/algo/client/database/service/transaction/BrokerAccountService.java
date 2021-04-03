package com.straders.algo.client.database.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.key.BrokerAccountKey;
import com.straders.algo.client.database.model.BrokerAccountModel;
import com.straders.algo.client.database.repository.BrokerAccountRepository;

@Service
public class BrokerAccountService {

	@Autowired
	public BrokerAccountRepository broker;

	public void save(BrokerAccountModel model) {
		broker.save(model);
	}

	public void saveAll(List<BrokerAccountModel> confirmList) {
		broker.saveAll(confirmList);
	}

	public Boolean existById(BrokerAccountKey key) {
		return broker.existsById(key);
	}

	public void delete(BrokerAccountKey confirmKey) {
		if (existById(confirmKey)) {
			broker.deleteById(confirmKey);
		}
	}

	public void deleteAll() {
		broker.deleteAll();
	}

}
