package com.ssrp.dto;

public record HealthStatusDto(
        String status,
        String backendVersion,
        String databaseStatus,
        String mlServiceStatus
) {}

