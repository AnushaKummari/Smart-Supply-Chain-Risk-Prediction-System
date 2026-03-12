package com.ssrp.dto;

public record SupplierCreateRequest(
        String supplierName,
        String contactEmail,
        String contactPhone
) {}
