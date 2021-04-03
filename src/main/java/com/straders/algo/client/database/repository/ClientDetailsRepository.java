package com.straders.algo.client.database.repository;

import java.sql.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.straders.algo.client.database.model.ClientDetailsModel;

@Repository
public interface ClientDetailsRepository extends JpaRepository<ClientDetailsModel, Date> {

}
