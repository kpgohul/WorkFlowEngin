package com.friends.actionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcAuditing(auditorAwareRef = "reactiveAuditorAware")
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ActionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActionserviceApplication.class, args);
	}

}
