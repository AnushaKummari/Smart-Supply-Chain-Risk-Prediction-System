package com.ssrp.services;

import com.ssrp.dto.analytics.AnalyticsOverviewResponse;
import com.ssrp.dto.analytics.DelayTrendsResponse;
import com.ssrp.dto.analytics.RiskDistributionResponse;
import com.ssrp.dto.analytics.SupplierPerformanceResponse;
import com.ssrp.repositories.ShipmentRepository;
import com.ssrp.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private final ShipmentRepository shipmentRepository;
    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse overview() {
        long total = shipmentRepository.count();
        long delivered = shipmentRepository.countByShipmentStatusIgnoreCase("DELIVERED");
        long inTransit = shipmentRepository.countByShipmentStatusIgnoreCase("IN_TRANSIT");
        long highRisk = shipmentRepository.countHighRisk(71.0);

        Double avgDelay = shipmentRepository.avgPredictedDelayHours();
        double avgDelaySafe = avgDelay != null ? avgDelay : 0.0;

        List<AnalyticsOverviewResponse.StatusCount> statusDistribution = shipmentRepository.statusCounts().stream()
                .map(row -> new AnalyticsOverviewResponse.StatusCount(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();

        return new AnalyticsOverviewResponse(
                total,
                delivered,
                inTransit,
                highRisk,
                round2(avgDelaySafe),
                statusDistribution
        );
    }

    @Transactional(readOnly = true)
    public RiskDistributionResponse riskDistribution() {
        List<RiskDistributionResponse.RiskCount> distribution = shipmentRepository.riskLevelCounts().stream()
                .map(row -> new RiskDistributionResponse.RiskCount(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
        return new RiskDistributionResponse(distribution);
    }

    @Transactional(readOnly = true)
    public SupplierPerformanceResponse supplierPerformance(int limit) {
        List<SupplierPerformanceResponse.SupplierPerformance> suppliers = supplierRepository.topSuppliersByReliability(limit).stream()
                .map(row -> new SupplierPerformanceResponse.SupplierPerformance(
                        row[0] != null ? ((Number) row[0]).longValue() : null,
                        (String) row[1],
                        row[2] != null ? ((Number) row[2]).doubleValue() : null,
                        row[3] != null ? ((Number) row[3]).longValue() : 0L,
                        row[4] != null ? round2(((Number) row[4]).doubleValue()) : null
                ))
                .toList();
        return new SupplierPerformanceResponse(suppliers);
    }

    @Transactional(readOnly = true)
    public DelayTrendsResponse delayTrends(int days) {
        OffsetDateTime since = OffsetDateTime.now(ZoneOffset.UTC).minusDays(Math.max(days, 1));

        List<DelayTrendsResponse.DelayTrendPoint> points = shipmentRepository.avgDelayByDaySince(since).stream()
                .map(row -> {
                    // row[0] comes as java.sql.Timestamp
                    String day = row[0].toString().substring(0, 10);
                    Double avg = row[1] != null ? ((Number) row[1]).doubleValue() : null;
                    return new DelayTrendsResponse.DelayTrendPoint(day, avg != null ? round2(avg) : null);
                })
                .toList();

        return new DelayTrendsResponse(points);
    }

    private static double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

