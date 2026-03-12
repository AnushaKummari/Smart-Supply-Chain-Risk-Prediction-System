package com.ssrp.dto;

import java.time.OffsetDateTime;

public record ShipmentResponse(
        Long id,
        Long supplierId,
        String supplierName,
        String sourceLocation,
        String destinationLocation,
        Double distanceKm,
        String vehicleType,
        OffsetDateTime dispatchTime,
        OffsetDateTime expectedDeliveryTime,
        OffsetDateTime actualDeliveryTime,
        String weatherCondition,
        String trafficLevel,
        String shipmentStatus,
        Double predictedDelayHours,
        Double delayProbability,
        Double riskScore,
        String riskLevel,
        OffsetDateTime createdAt
) {}
