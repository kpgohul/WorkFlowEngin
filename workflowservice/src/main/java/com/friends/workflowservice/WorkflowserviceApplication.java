package com.friends.workflowservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan(basePackages = "com.friends.workflowservice.entity")
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WorkflowserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkflowserviceApplication.class, args);
    }
}
