package com.ssrp.dto;

import java.util.List;

public record RouteOptimizationResponse(
        Long shipmentId,
        Double distanceKm,
        String trafficLevel,
        String weatherCondition,
        Double predictedDelayHours,
        Double riskScore,
        List<RouteSuggestion> suggestions
) {
    public record RouteSuggestion(
            String type,
            String title,
            String description,
            String priority
    ) {}
}

