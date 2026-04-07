package com.friends.userservice.path;

public class ApiRoutes {

    private ApiRoutes() {}

    public static final String API_BASE = "/api";
    public static final String API_V1   = "/v1";
    public static final String BASE     = API_BASE + API_V1;

    // Users
    public static final String BASE_USERS              = BASE + "/users";
    public static final String USERS_ME                = BASE_USERS + "/me";
    public static final String USERS_BY_ID             = BASE_USERS + "/{id}";
    public static final String USERS_BY_ACCOUNT_ID     = BASE_USERS + "/account/{accountId}";
    public static final String USERS_DEACTIVATE        = BASE_USERS + "/{id}/deactivate";

    // Teams
    public static final String BASE_TEAMS              = BASE + "/teams";
    public static final String TEAMS_BY_ID             = BASE_TEAMS + "/{id}";
    public static final String TEAMS_ASSIGN            = BASE_TEAMS + "/assign";
    public static final String TEAMS_REMOVE_USER       = BASE_TEAMS + "/remove/{userId}";
    public static final String TEAMS_MEMBERS           = BASE_TEAMS + "/{teamId}/members";
    public static final String TEAMS_USER_ASSIGNMENT   = BASE_TEAMS + "/assignment/{userId}";
}
