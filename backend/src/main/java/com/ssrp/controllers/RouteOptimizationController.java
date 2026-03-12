package com.ssrp.controllers;

import com.ssrp.dto.RouteOptimizationResponse;
import com.ssrp.services.RouteOptimizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RouteOptimizationController {

    private final RouteOptimizationService routeOptimizationService;

    @GetMapping("/optimize/shipment/{id}")
    public ResponseEntity<RouteOptimizationResponse> optimizeForShipment(@PathVariable("id") Long shipmentId) {
        return ResponseEntity.ok(routeOptimizationService.optimizeForShipment(shipmentId));
    }
}

