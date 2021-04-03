package com.straders.algo.client.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straders.algo.client.database.key.BrokerAccountKey;
import com.straders.algo.client.database.model.BrokerAccountModel;

@Repository
public interface BrokerAccountRepository extends JpaRepository<BrokerAccountModel, BrokerAccountKey> {

}
