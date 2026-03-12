package com.ssrp.dto.analytics;

import java.util.List;

public record RiskDistributionResponse(
        List<RiskCount> distribution
) {
    public record RiskCount(String riskLevel, long count) {}
}

