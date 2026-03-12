package com.ssrp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DelayPredictionResponse(
        @JsonProperty("delay_probability") Double delayProbability,
        @JsonProperty("predicted_delay_hours") Double predictedDelayHours
) {}

