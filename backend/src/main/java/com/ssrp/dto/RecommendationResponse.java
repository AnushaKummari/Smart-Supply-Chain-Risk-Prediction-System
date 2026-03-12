package com.ssrp.dto;

import java.util.List;

public record RecommendationResponse(
        Long shipmentId,
        String riskLevel,
        Double riskScore,
        Double predictedDelayHours,
        Double delayProbability,
        List<RecommendationItem> recommendations
) {
    public record RecommendationItem(
            String type,
            String title,
            String description,
            String severity
    ) {}
}

