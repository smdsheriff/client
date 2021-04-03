package com.straders.algo.client.database.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.key.ConfigurationKey;
import com.straders.algo.client.database.model.ConfigurationModel;
import com.straders.algo.client.database.repository.ConfigurationRepository;

@Service
public class ConfigurationService {

	@Autowired
	public ConfigurationRepository config;

	public void save(ConfigurationModel model) {
		config.save(model);
	}

	public void saveAll(List<ConfigurationModel> confirmList) {
		config.saveAll(confirmList);
	}

	public Boolean existById(ConfigurationKey key) {
		return config.existsById(key);
	}
	
	public void delete(ConfigurationKey confirmKey) {
		if (existById(confirmKey)) {
			config.deleteById(confirmKey);
		}
	}

	public void deleteAll() {
		config.deleteAll();
	}

}
