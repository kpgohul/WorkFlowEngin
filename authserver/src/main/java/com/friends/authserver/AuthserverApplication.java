package com.friends.authserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import javax.swing.plaf.synth.SynthOptionPaneUI;

@EnableJpaRepositories
@EnableJpaAuditing
@EnableWebSecurity
@SpringBootApplication
public class AuthserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthserverApplication.class, args);
	}

	@Bean
	public CommandLineRunner debugRegisteredClients(RegisteredClientRepository repo) {
		return args -> {
			RegisteredClient auth = repo.findByClientId("AuthCodeClient");
			RegisteredClient pkce = repo.findByClientId("PKCEClient");

			System.out.println();
			System.out.println("========== AUTH CLIENT ==========");
			System.out.println("Client ID: " + auth.getClientId());
			System.out.println("Auth Methods: " + auth.getClientAuthenticationMethods());
			System.out.println("Grant Types: " + auth.getAuthorizationGrantTypes());
			System.out.println("Scopes: " + auth.getScopes());
			System.out.println("Require Proof Key: " + auth.getClientSettings().isRequireProofKey());

			System.out.println("========== PKCE CLIENT ==========");
			System.out.println("Client ID: " + pkce.getClientId());
			System.out.println("Auth Methods: " + pkce.getClientAuthenticationMethods());
			System.out.println("Grant Types: " + pkce.getAuthorizationGrantTypes());
			System.out.println("Scopes: " + pkce.getScopes());
			System.out.println("Require Proof Key: " + pkce.getClientSettings().isRequireProofKey());
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

}
