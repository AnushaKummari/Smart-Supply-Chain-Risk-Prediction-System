package com.ssrp.dto.analytics;

import java.util.List;

public record SupplierPerformanceResponse(
        List<SupplierPerformance> suppliers
) {
    public record SupplierPerformance(
            Long supplierId,
            String supplierName,
            Double reliabilityScore,
            long shipmentCount,
            Double averageRiskScore
    ) {}
}

