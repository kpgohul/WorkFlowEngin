package com.friends.authserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@EnableJpaRepositories
@EnableWebSecurity
@SpringBootApplication
public class AuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthserverApplication.class, args);
	}

	@Bean
	public CommandLineRunner debugRegisteredClients(
			RegisteredClientRepository repo,
			@Value("${auth.client.auth-code.id}") String authCodeClientId,
			@Value("${auth.client.pkce.id}") String pkceClientId,
			@Value("${auth.client.service.id}") String serviceClientId
	) {
		return args -> {
			RegisteredClient auth = repo.findByClientId(authCodeClientId);
			RegisteredClient pkce = repo.findByClientId(pkceClientId);
			RegisteredClient clientCredentials = repo.findByClientId(serviceClientId);

			System.out.println();
			System.out.println("========== AUTH CLIENT ==========");
			if (auth != null) {
				System.out.println("Client ID: " + auth.getClientId());
				System.out.println("Auth Methods: " + auth.getClientAuthenticationMethods());
				System.out.println("Grant Types: " + auth.getAuthorizationGrantTypes());
				System.out.println("Scopes: " + auth.getScopes());
				System.out.println("Require Proof Key: " + auth.getClientSettings().isRequireProofKey());
			} else {
				System.out.println("NOT FOUND for clientId: " + authCodeClientId);
			}

			System.out.println();

			System.out.println("========== PKCE CLIENT ==========");
			if (pkce != null) {
				System.out.println("Client ID: " + pkce.getClientId());
				System.out.println("Auth Methods: " + pkce.getClientAuthenticationMethods());
				System.out.println("Grant Types: " + pkce.getAuthorizationGrantTypes());
				System.out.println("Scopes: " + pkce.getScopes());
				System.out.println("Require Proof Key: " + pkce.getClientSettings().isRequireProofKey());
			} else {
				System.out.println("NOT FOUND for clientId: " + pkceClientId);
			}

			System.out.println();

			System.out.println("========== CLIENT CREDENTIALS CLIENT ==========");
			if (clientCredentials != null) {
				System.out.println("Client ID: " + clientCredentials.getClientId());
				System.out.println("Auth Methods: " + clientCredentials.getClientAuthenticationMethods());
				System.out.println("Grant Types: " + clientCredentials.getAuthorizationGrantTypes());
				System.out.println("Scopes: " + clientCredentials.getScopes());
			} else {
				System.out.println("NOT FOUND for clientId: " + serviceClientId);
			}

		};
	}

	@Bean
	public CommandLineRunner debugMailConfig(
			@Value("${spring.mail.host:NOT_SET}") String host,
			@Value("${spring.mail.port:NOT_SET}") String port,
			@Value("${spring.mail.username:NOT_SET}") String username,
			@Value("${spring.mail.password:NOT_SET}") String password,
			@Value("${app.mail.from:NOT_SET}") String from
	) {
		return args -> {
			System.out.println();
			System.out.println("========== MAIL CONFIGURATION ==========");
			System.out.println("MAIL HOST = " + host);
			System.out.println("MAIL PORT = " + port);
			System.out.println("MAIL USERNAME = " + username);
			System.out.println("MAIL PASSWORD EMPTY? = " + (password == null || password.isBlank()));
			System.out.println("APP MAIL FROM = " + from);
			System.out.println();
		};
	}

	@Bean
	public CommandLineRunner kafkaConfig(
			@Value("${spring.kafka.bootstrap-servers}") String kafkaBootstrapServers
	) {
		return args -> {
			System.out.println();
			System.out.println("========== Kafka Configs ==========");
			System.out.println("BOOTSTRAP SERVER = " + kafkaBootstrapServers);
			System.out.println();
		};
	}

}
