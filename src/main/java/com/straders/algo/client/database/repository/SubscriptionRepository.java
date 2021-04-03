package com.straders.algo.client.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straders.algo.client.database.model.SubscriptionModel;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionModel, String> {

}
