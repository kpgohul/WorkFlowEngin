package com.friends.executionservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
@EnableR2dbcRepositories
@EnableR2dbcAuditing(auditorAwareRef = "reactiveAuditorAware")
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class ExecutionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExecutionserviceApplication.class, args);
	}

	@Bean
	public CommandLineRunner logStartupDetails(
			@Value("${spring.r2dbc.url}") String r2dbcUrl,
			@Value("${spring.r2dbc.username}") String r2dbcUsername,
			@Value("${spring.r2dbc.password}") String r2dbcPassword,
			@Value("${spring.r2dbc.pool.initial-size}") int initialPoolSize,
			@Value("${spring.r2dbc.pool.max-size}") int maxPoolSize,

			@Value("${kafka.bootstrap-servers}") String kafkaServers,
			@Value("${kafka.execution.topic}") String executionTopic,
			@Value("${kafka.result.topic}") String resultTopic,
			@Value("${kafka.result.group-id}") String groupId,

			@Value("${app.gateway-url}") String gatewayBase,

			@Value("${server.port}") String serverPort
	) {
		return args -> {
			System.out.println("\n================ APPLICATION CONFIGURATION ================\n");

			// DATABASE
			System.out.println("========== DATABASE ==========");
			System.out.println("R2DBC URL            = " + r2dbcUrl);
			System.out.println("R2DBC USERNAME       = " + r2dbcUsername);
			System.out.println("PASSWORD EMPTY?      = " + (r2dbcPassword == null || r2dbcPassword.isEmpty()));
			System.out.println("POOL INITIAL SIZE    = " + initialPoolSize);
			System.out.println("POOL MAX SIZE        = " + maxPoolSize);

			// KAFKA
			System.out.println("\n========== KAFKA ==========");
			System.out.println("BOOTSTRAP SERVERS    = " + kafkaServers);
			System.out.println("EXECUTION TOPIC      = " + executionTopic);
			System.out.println("RESULT TOPIC         = " + resultTopic);
			System.out.println("GROUP ID             = " + groupId);

			// CLIENTS
			System.out.println("\n========== CLIENTS ==========");
			System.out.println("GATEWAY BASE URL = " + gatewayBase);

			// SERVER
			System.out.println("\n========== SERVER ==========");
			System.out.println("PORT                 = " + serverPort);

			System.out.println("\n===========================================================\n");
		};
	}
}
