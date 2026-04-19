package com.friends.actionservice.util;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final JsonMapper objectMapper;

    public String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JacksonException e) {
            throw new IllegalArgumentException("Failed to serialize JSON", e);
        }
    }
}

