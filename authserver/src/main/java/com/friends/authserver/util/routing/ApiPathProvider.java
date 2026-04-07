package com.friends.authserver.util.routing;

import org.springframework.stereotype.Component;

@Component("apiPaths")
public class ApiPathProvider {

    public String getAuthLogin() {
        return ApiRoutes.AUTH_LOGIN;
    }

    public String getAuthForgotPassword() {
        return ApiRoutes.AUTH_FORGOT_PASSWORD;
    }

    public String getAuthResetPassword() {
        return ApiRoutes.AUTH_RESET_PASSWORD;
    }

    public String getAuthLogout() {
        return ApiRoutes.AUTH_LOGOUT;
    }

    public String getAccountsRegister() {
        return ApiRoutes.ACCOUNTS_REGISTER;
    }

    public String getAccountsHome() {
        return ApiRoutes.ACCOUNTS_HOME;
    }

    public String getAccountsSettings() {
        return ApiRoutes.ACCOUNTS_SETTINGS;
    }

    public String getAccountsDelete() {
        return ApiRoutes.ACCOUNTS_DELETE;
    }
}
