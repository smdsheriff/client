package com.straders.algo.client.database.service.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.model.UserAccountModel;
import com.straders.algo.client.database.repository.UserAccountRepository;

@Service
public class UserAccountService {

	@Autowired
	UserAccountRepository user;

	public List<UserAccountModel> findAll() {
		return user.findAll();
	}

	public void saveAll(List<UserAccountModel> modelList) {
		user.saveAll(modelList);
	}

	public void save(UserAccountModel model) {
		user.save(model);
	}

	public Optional<UserAccountModel> findById(String key) {
		return user.findById(key);
	}

	public void deleteAll() {
		user.deleteAll();
	}

}
