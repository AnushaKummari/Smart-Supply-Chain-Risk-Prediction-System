package com.ssrp.services;

import com.ssrp.dto.SupplierCreateRequest;
import com.ssrp.dto.SupplierListResponse;
import com.ssrp.entities.Supplier;
import com.ssrp.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public List<SupplierListResponse> findAll() {
        return supplierRepository.findAll().stream()
                .map(s -> new SupplierListResponse(s.getId(), s.getSupplierName(), s.getContactEmail()))
                .toList();
    }

    @Transactional
    public SupplierListResponse create(SupplierCreateRequest request) {
        Supplier supplier = Supplier.builder()
                .supplierName(request.supplierName())
                .contactEmail(request.contactEmail())
                .contactPhone(request.contactPhone())
                .createdAt(OffsetDateTime.now())
                .build();
        supplier = supplierRepository.save(supplier);
        return new SupplierListResponse(supplier.getId(), supplier.getSupplierName(), supplier.getContactEmail());
    }
}
