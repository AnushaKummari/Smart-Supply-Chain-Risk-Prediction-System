package com.ssrp.dto;

import java.time.OffsetDateTime;

public record AlertResponse(
        Long id,
        String alertType,
        String alertMessage,
        String severity,
        Long shipmentId,
        Long supplierId,
        Long inventoryId,
        OffsetDateTime createdAt
) {}

