package com.ssrp.controllers;

import com.ssrp.dto.SupplierCreateRequest;
import com.ssrp.dto.SupplierListResponse;
import com.ssrp.services.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<List<SupplierListResponse>> list() {
        return ResponseEntity.ok(supplierService.findAll());
    }

    @PostMapping
    public ResponseEntity<SupplierListResponse> create(@RequestBody SupplierCreateRequest request) {
        SupplierListResponse created = supplierService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
