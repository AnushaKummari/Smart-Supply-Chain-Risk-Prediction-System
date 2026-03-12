package com.ssrp.services;

import com.ssrp.dto.DelayPredictionRequest;
import com.ssrp.dto.DelayPredictionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class MlPredictionClient {

    private final WebClient webClient;

    public MlPredictionClient(@Value("${ssrp.ml-service.base-url}") String mlBaseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(mlBaseUrl)
                .build();
    }

    public Mono<DelayPredictionResponse> predictDelay(DelayPredictionRequest request) {
        return webClient.post()
                .uri("/predict-delay")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(DelayPredictionResponse.class);
    }
}