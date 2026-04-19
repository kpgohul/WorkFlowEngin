package com.friends.authserver.config;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.friends.authserver.util.accountutil.AccountStateValidator;
import com.friends.authserver.path.ApiRoutes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Configuration
public class AuthServerConfig {

    private static final String LOGIN_NOTICE_LOGOUT = "LOGIN_NOTICE_LOGOUT";
    private static final String LOGIN_NOTICE_SESSION_EXPIRED = "LOGIN_NOTICE_SESSION_EXPIRED";

    @Value("${auth.client.auth-code.id}")
    private String authCodeClientId;

    @Value("${auth.client.auth-code.secret}")
    private String authCodeClientSecret;

    @Value("${auth.client.auth-code.redirect-uri}")
    private String authCodeRedirectUri;

    @Value("${auth.client.pkce.id}")
    private String pkceClientId;

    @Value("${auth.client.pkce.redirect-uri}")
    private String pkceRedirectUri;

    @Value("${auth.client.service.id}")
    private String serviceClientId;

    @Value("${auth.client.service.secret}")
    private String serviceClientSecret;

    @Value("${auth.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();
        http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
                .with(authorizationServerConfigurer, (authorizationServer) ->
                        authorizationServer
                                .oidc(Customizer.withDefaults())    // Enable OpenID Connect 1.0
                )
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .anyRequest().authenticated()
                )    .exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                        new LoginUrlAuthenticationEntryPoint(ApiRoutes.AUTH_LOGIN),
                        new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                ));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        // Allow unauthenticated access to specific auth endpoints
                        .requestMatchers(ApiRoutes.AUTH_LOGIN, ApiRoutes.AUTH_FORGOT_PASSWORD, ApiRoutes.AUTH_RESET_PASSWORD, ApiRoutes.ACCOUNTS_REGISTER,
                                "/css/**", "/js/**", "/images/**", "/error")
                        .permitAll()
                        // Require authentication for protected account and auth endpoints
                        .requestMatchers(ApiRoutes.AUTH_LOGOUT, ApiRoutes.AUTH_BASE + "/session-extend", ApiRoutes.ACCOUNTS_SETTINGS, ApiRoutes.ACCOUNTS_DELETE)
                        .authenticated()
                        .anyRequest()
                        .authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession() // Migrate session on authentication to prevent session fixation
                        .invalidSessionStrategy((request, response) -> handleInvalidSession(request, response))
                        .maximumSessions(1) // Allow maximum 1 concurrent session per user
                        .maxSessionsPreventsLogin(false) // Don't prevent login, just invalidate old sessions
                        .sessionRegistry(sessionRegistry())
                        .expiredSessionStrategy(event -> handleInvalidSession(event.getRequest(), event.getResponse())))
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // Prevent clickjacking
                        .contentTypeOptions(Customizer.withDefaults()) // Prevent MIME sniffing
                        .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                                .maxAgeInSeconds(31536000) // 1 year
                                .includeSubDomains(true)
                        )
                )
                .formLogin(form -> form
                        .loginPage(ApiRoutes.AUTH_LOGIN)
                        .loginProcessingUrl(ApiRoutes.AUTH_LOGIN)
                        .successHandler(formLoginSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl(ApiRoutes.AUTH_LOGOUT)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            request.getSession(true).setAttribute(LOGIN_NOTICE_LOGOUT, true);
                            response.sendRedirect(ApiRoutes.AUTH_LOGIN);
                        })
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll());

        System.out.println("serviceClientId = " + serviceClientId);

        return http.build();
    }//http://localhost:9099/.well-known/openid-configuration

    private void handleInvalidSession(HttpServletRequest request, HttpServletResponse response) {
        String requestPath = request.getRequestURI();
        if (isPublicAuthPath(requestPath)) {
            clearSessionCookie(request, response);
            String redirectTarget = requestPath;
            String query = request.getQueryString();
            if (query != null && !query.isBlank()) {
                redirectTarget += "?" + query;
            }
            sendRedirect(response, redirectTarget);
            return;
        }

        request.getSession(true).setAttribute(LOGIN_NOTICE_SESSION_EXPIRED, true);
        sendRedirect(response, ApiRoutes.AUTH_LOGIN);
    }

    private void sendRedirect(HttpServletResponse response, String target) {
        try {
            response.sendRedirect(target);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to redirect for invalid session", ex);
        }
    }

    private boolean isPublicAuthPath(String requestPath) {
        return requestPath.equals(ApiRoutes.AUTH_LOGIN)
                || requestPath.equals(ApiRoutes.AUTH_FORGOT_PASSWORD)
                || requestPath.equals(ApiRoutes.AUTH_RESET_PASSWORD)
                || requestPath.equals(ApiRoutes.ACCOUNTS_REGISTER);
    }

    private void clearSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0);
        cookie.setPath(request.getContextPath().isEmpty() ? "/" : request.getContextPath());
        response.addCookie(cookie);
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler formLoginSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(ApiRoutes.ACCOUNTS_HOME);
        handler.setAlwaysUseDefaultTargetUrl(false);
        return handler;
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient authCodeClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(authCodeClientId)
                .clientSecret(passwordEncoder().encode(authCodeClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(authCodeRedirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.EMAIL)
//                .scope("offline_access")
                .tokenSettings(TokenSettings.builder().accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofDays(1)).refreshTokenTimeToLive(Duration.ofDays(30))
                        .reuseRefreshTokens(true)
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .build())
                .build();

        RegisteredClient pkceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(pkceClientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri(pkceRedirectUri)
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.EMAIL)
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofMinutes(5))
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        .reuseRefreshTokens(true)
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(true)
                        .build())
                .build();

        RegisteredClient serviceClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(serviceClientId)
                .clientSecret(passwordEncoder().encode(serviceClientSecret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .scope("service")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(authCodeClient, pkceClient, serviceClient);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AccountDetailsService service,
            PasswordEncoder encoder,
            AccountStateValidator accountStateValidator
    ) {
        AuthProvider provider = new AuthProvider(service, encoder, accountStateValidator);
        ProviderManager manager = new ProviderManager(provider);
        manager.setEraseCredentialsAfterAuthentication(true);
        return manager;
    }

    @Bean
    @Primary
    public JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }


}