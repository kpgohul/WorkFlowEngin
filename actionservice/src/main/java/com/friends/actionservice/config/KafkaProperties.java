package com.friends.actionservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootstrapServers;

    private Actions actions = new Actions();
    private Results results = new Results();

    @Data
    public static class Actions {
        private String topic;
        private String groupId;
    }

    @Data
    public static class Results {
        private String topic;
    }
}

