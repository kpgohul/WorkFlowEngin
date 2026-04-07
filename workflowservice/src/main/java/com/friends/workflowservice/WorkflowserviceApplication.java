package com.friends.workflowservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@EntityScan(basePackages = "com.friends.workflowservice.entity")
@EnableR2dbcAuditing(auditorAwareRef = "reactiveAuditorAware")
@SpringBootApplication
public class WorkFlowServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkFlowServiceApplication.class, args);
    }
}
