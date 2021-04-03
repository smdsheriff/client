package com.straders.algo.client.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straders.algo.client.database.key.ProfitDetailsKey;
import com.straders.algo.client.database.model.ProfitDetailsModel;

@Repository
public interface ProfitDetailsRepository extends JpaRepository<ProfitDetailsModel, ProfitDetailsKey> {

}
