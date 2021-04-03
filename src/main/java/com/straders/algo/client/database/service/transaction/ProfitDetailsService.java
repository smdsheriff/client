package com.straders.algo.client.database.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.key.ProfitDetailsKey;
import com.straders.algo.client.database.model.ProfitDetailsModel;
import com.straders.algo.client.database.repository.ProfitDetailsRepository;

@Service
public class ProfitDetailsService {

	@Autowired
	public ProfitDetailsRepository profit;

	public void save(ProfitDetailsModel model) {
		profit.save(model);
	}

	public void saveAll(List<ProfitDetailsModel> confirmList) {
		profit.saveAll(confirmList);
	}

	public Boolean existById(ProfitDetailsKey key) {
		return profit.existsById(key);
	}

	public void delete(ProfitDetailsKey confirmKey) {
		if (existById(confirmKey)) {
			profit.deleteById(confirmKey);
		}
	}

	public void deleteAll() {
		profit.deleteAll();
	}

}
