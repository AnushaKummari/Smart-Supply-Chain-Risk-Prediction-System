package com.ssrp.controllers;

import com.ssrp.dto.analytics.AnalyticsOverviewResponse;
import com.ssrp.dto.analytics.DelayTrendsResponse;
import com.ssrp.dto.analytics.RiskDistributionResponse;
import com.ssrp.dto.analytics.SupplierPerformanceResponse;
import com.ssrp.services.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/overview")
    public ResponseEntity<AnalyticsOverviewResponse> overview() {
        return ResponseEntity.ok(analyticsService.overview());
    }

    @GetMapping("/risk-distribution")
    public ResponseEntity<RiskDistributionResponse> riskDistribution() {
        return ResponseEntity.ok(analyticsService.riskDistribution());
    }

    @GetMapping("/supplier-performance")
    public ResponseEntity<SupplierPerformanceResponse> supplierPerformance(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(analyticsService.supplierPerformance(limit));
    }

    @GetMapping("/delay-trends")
    public ResponseEntity<DelayTrendsResponse> delayTrends(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(analyticsService.delayTrends(days));
    }
}

