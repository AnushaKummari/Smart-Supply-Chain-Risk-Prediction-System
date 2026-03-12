package com.ssrp.controllers;

import com.ssrp.dto.RecommendationResponse;
import com.ssrp.services.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/shipment/{id}")
    public ResponseEntity<RecommendationResponse> recommendationsForShipment(@PathVariable("id") Long shipmentId) {
        return ResponseEntity.ok(recommendationService.recommendForShipment(shipmentId));
    }
}

