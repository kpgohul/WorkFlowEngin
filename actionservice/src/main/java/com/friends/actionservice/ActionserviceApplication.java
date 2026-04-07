package com.friends.actionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@SpringBootApplication
@EnableR2dbcAuditing(auditorAwareRef = "reactiveAuditorAware")
public class ActionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActionserviceApplication.class, args);
	}

}
