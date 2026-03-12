package com.ssrp.dto.analytics;

import java.util.List;

public record AnalyticsOverviewResponse(
        long totalShipments,
        long deliveredShipments,
        long inTransitShipments,
        long highRiskShipments,
        double averagePredictedDelayHours,
        List<StatusCount> statusDistribution
) {
    public record StatusCount(String status, long count) {}
}

