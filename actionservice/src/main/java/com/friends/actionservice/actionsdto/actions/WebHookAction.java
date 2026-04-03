package com.friends.actionservice.actionsdto.actions;

import com.friends.actionservice.actionsdto.ActionRequest;
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
public class WebHookAction extends ActionRequest {

    private String name;
    private URI uri;
    private HttpMethod method;
    private Map<String, Object> header;
    private String body;
}
