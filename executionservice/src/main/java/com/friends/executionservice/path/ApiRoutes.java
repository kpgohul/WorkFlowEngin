package com.friends.executionservice.path;

public final class ApiRoutes {

    private ApiRoutes() {}

    public static final String API_BASE = "/api";
    public static final String API_V1   = "/v1";
    public static final String BASE     = API_BASE + API_V1;

    // Executions
    public static final String BASE_EXECUTIONS              = BASE + "/executions";
    public static final String EXECUTIONS_BY_ID             = BASE_EXECUTIONS + "/{id}";
    public static final String EXECUTIONS_CANCEL            = BASE_EXECUTIONS + "/{id}/cancel";

}

