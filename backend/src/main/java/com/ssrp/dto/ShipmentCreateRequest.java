package com.ssrp.dto;

import java.time.OffsetDateTime;

public record ShipmentCreateRequest(
        Long supplierId,
        String sourceLocation,
        String destinationLocation,
        Double distanceKm,
        String vehicleType,
        OffsetDateTime dispatchTime,
        OffsetDateTime expectedDeliveryTime,
        String weatherCondition,
        String trafficLevel
) {}
