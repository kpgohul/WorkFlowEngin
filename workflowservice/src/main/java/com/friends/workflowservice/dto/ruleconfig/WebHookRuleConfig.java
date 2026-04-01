package com.friends.workflowservice.dto.ruleconfig;

import com.friends.workflowservice.appconstant.ApprovalType;
import com.friends.workflowservice.appconstant.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import javax.sql.rowset.spi.SyncResolver;
import java.net.URI;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebHookRuleConfig implements RuleConfig{

    @NotBlank(message = "WebHook name required.")
    private String name;
    @NotNull(message = "WebHook url is required")
    private URI uri;
    @NotNull(message = "WebHook http method is required.")
    private HttpMethod method;
    @NotNull(message = "WebHook header is required.")
    private Map<String, Object> header;
    @NotBlank(message = "WebHook body is require.")
    private String body;
}
