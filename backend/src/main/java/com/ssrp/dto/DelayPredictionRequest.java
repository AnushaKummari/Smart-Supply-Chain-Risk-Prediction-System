package com.ssrp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record DelayPredictionRequest(
        @JsonProperty("distance_km") Double distanceKm,
        @JsonProperty("traffic_level") String trafficLevel,
        @JsonProperty("weather_condition") String weatherCondition,
        @JsonProperty("supplier_reliability") Double supplierReliability,
        @JsonProperty("vehicle_type") String vehicleType,
        @JsonProperty("dispatch_time") OffsetDateTime dispatchTime
) {}

