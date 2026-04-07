package com.friends.authserver.util.routing;

public final class ApiRoutes {

    private ApiRoutes() {
    }

    public static final String API_BASE = "/api";
    public static final String API_V1 = "/v1";
    public static final String BASE = API_BASE + API_V1;

    public static final String AUTH_BASE = BASE + "/auth";
    public static final String AUTH_LOGIN = AUTH_BASE + "/login";
    public static final String AUTH_FORGOT_PASSWORD = AUTH_BASE + "/forgot-password";
    public static final String AUTH_RESET_PASSWORD = AUTH_BASE + "/reset-password";
    public static final String AUTH_LOGOUT = AUTH_BASE + "/logout";

    public static final String ACCOUNTS_BASE = BASE + "/accounts";
    public static final String ACCOUNTS_REGISTER = ACCOUNTS_BASE + "/register";
    public static final String ACCOUNTS_HOME = ACCOUNTS_BASE + "/home";
    public static final String ACCOUNTS_SETTINGS = ACCOUNTS_BASE + "/settings";
    public static final String ACCOUNTS_DELETE = ACCOUNTS_BASE + "/delete";
}
