package com.straders.algo.client.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straders.algo.client.database.key.ConfigurationKey;
import com.straders.algo.client.database.model.ConfigurationModel;

@Repository
public interface ConfigurationRepository extends JpaRepository<ConfigurationModel, ConfigurationKey> {

}
