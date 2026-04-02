package com.friends.executionservice.config;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

@Configuration
public class ExecutionAppConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Bean
    public KafkaSender<String, String> kafkaSender(
            @Value("${kafka.bootstrap-servers:localhost:9092}") String bootstrapServers) {

        var producerConfig = Map.<String, Object>of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
//                SaslConfigs.SASL_MECHANISM, "PLAIN",
//
//                ProducerConfig.ACKS_CONFIG, "all"
        );

        return KafkaSender.create(SenderOptions.create(producerConfig));
    }

    @Bean
    public KafkaReceiver<String, String> kafkaReceiver(
            @Value("${kafka.bootstrap-servers:localhost:9092}") String bootstrapServers,
            @Value("${kafka.execution.group-id:execution-service-group}") String groupId,
            @Value("${kafka.execution.topic:workflow-execution-event}") String topic) {

        var consumerConfig = Map.<String, Object>of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, groupId,
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
        );

        var options = ReceiverOptions.<String, String>create(consumerConfig)
                .subscription(List.of(topic));

        return KafkaReceiver.create(options);
    }
}
