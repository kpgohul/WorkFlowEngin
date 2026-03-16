package service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class SlackService {

    private final WebClient webClient;

    public void sendSlackMessage(String message) {

        webClient.post()
                .uri("https://hooks.slack.com/services/YOUR_WEBHOOK")
                .bodyValue("{\"text\":\"" + message + "\"}")
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}