package com.friends.executionservice.clientdto.workflowclientdto.ruleconfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebHookRuleConfig implements RuleConfig{

    private String name;
    private URI uri;
    private HttpMethod method;
    private Map<String, Object> header;
    private String body;
}
