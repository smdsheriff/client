package com.straders.algo.client.database.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.straders.algo.client.database.key.OrderDetailsKey;
import com.straders.algo.client.database.model.OrderDetailsModel;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetailsModel, OrderDetailsKey> {

	@Query("SELECT t from OrderDetailsModel t WHERE t.date = :date and t.userId = :userId and t.symbol = :symbol")
	public List<OrderDetailsModel> getUserOrder(@Param("date") Date date, @Param("userId") String userId,
			@Param("symbol") String symbol);

}
