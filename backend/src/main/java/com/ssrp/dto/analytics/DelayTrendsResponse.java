package com.ssrp.dto.analytics;

import java.util.List;

public record DelayTrendsResponse(
        List<DelayTrendPoint> points
) {
    public record DelayTrendPoint(String date, Double averagePredictedDelayHours) {}
}

