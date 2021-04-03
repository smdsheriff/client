package com.straders.algo.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.straders.service.algobase.config.AlgoBase;

@SpringBootApplication
@ComponentScan(basePackages = { "com.straders.algo.client" })
@EntityScan("com.straders.algo.client.database.model")
@EnableJpaRepositories("com.straders.algo.client.database.repository")
@Import(AlgoBase.class)
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}

}
