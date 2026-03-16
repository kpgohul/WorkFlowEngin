package com.friends.executionservice.kafka;

import com.friends.executionservice.entity.Execution;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaSender<String, Execution> kafkaSender;

    public void publishWorkflowStarted(Execution execution) {
        var record = new ProducerRecord<>("topic", null, execution.getId().toString(), execution, null);
        var senderRecord = SenderRecord.create(record, record.key());
        kafkaSender.send(Mono.just(senderRecord));
    }

}