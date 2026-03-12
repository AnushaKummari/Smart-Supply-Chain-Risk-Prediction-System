package com.ssrp.controllers;

import com.ssrp.dto.ShipmentCreateRequest;
import com.ssrp.dto.ShipmentListResponse;
import com.ssrp.dto.ShipmentResponse;
import com.ssrp.dto.ShipmentUpdateStatusRequest;
import com.ssrp.services.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(@RequestBody ShipmentCreateRequest request) {
        ShipmentResponse created = shipmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentListResponse>> list(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(shipmentService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody ShipmentUpdateStatusRequest request) {
        return ResponseEntity.ok(shipmentService.updateStatus(id, request));
    }

    @PostMapping("/{id}/predict-delay")
    public ResponseEntity<ShipmentResponse> predictDelay(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.predictDelay(id));
    }
}
