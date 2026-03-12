package com.ssrp.dto;

import java.time.OffsetDateTime;

public record ShipmentListResponse(
        Long id,
        String supplierName,
        String sourceLocation,
        String destinationLocation,
        String vehicleType,
        String shipmentStatus,
        Double riskScore,
        String riskLevel,
        OffsetDateTime dispatchTime,
        OffsetDateTime expectedDeliveryTime
) {}
