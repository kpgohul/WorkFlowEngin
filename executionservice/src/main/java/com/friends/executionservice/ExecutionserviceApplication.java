package com.friends.executionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class ExecutionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExecutionserviceApplication.class, args);
	}

}
