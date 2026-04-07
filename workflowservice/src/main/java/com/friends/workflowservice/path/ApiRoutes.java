package com.friends.workflowservice.path;

public final class ApiRoutes {

    private ApiRoutes() {}

    public static final String API_BASE = "/api";
    public static final String API_V1   = "/v1";
    public static final String BASE     = API_BASE + API_V1;

    // Workflows
    public static final String BASE_WORKFLOWS          = BASE + "/workflows";
    // Workflow Types
    public static final String BASE_WORKFLOW_TYPES     = BASE + "/workflow-types";
}

