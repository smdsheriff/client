package com.straders.algo.client.database.service.transaction;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.straders.algo.client.database.model.ClientDetailsModel;
import com.straders.algo.client.database.repository.ClientDetailsRepository;

@Service
public class ClientDetailsService {

	@Autowired
	public ClientDetailsRepository order;

	public List<ClientDetailsModel> findAll() {
		return order.findAll();
	}
}
