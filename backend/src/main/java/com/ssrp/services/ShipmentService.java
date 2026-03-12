package com.ssrp.services;

import com.ssrp.dto.DelayPredictionRequest;
import com.ssrp.dto.DelayPredictionResponse;
import com.ssrp.dto.ShipmentCreateRequest;
import com.ssrp.dto.ShipmentListResponse;
import com.ssrp.dto.ShipmentResponse;
import com.ssrp.dto.ShipmentUpdateStatusRequest;
import com.ssrp.entities.Prediction;
import com.ssrp.entities.Shipment;
import com.ssrp.entities.Supplier;
import com.ssrp.repositories.PredictionRepository;
import com.ssrp.repositories.ShipmentRepository;
import com.ssrp.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final SupplierRepository supplierRepository;
    private final PredictionRepository predictionRepository;
    private final MlPredictionClient mlPredictionClient;
    private final AlertService alertService;

    @Transactional
    public ShipmentResponse create(ShipmentCreateRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + request.supplierId()));

        Shipment shipment = Shipment.builder()
                .supplier(supplier)
                .sourceLocation(request.sourceLocation())
                .destinationLocation(request.destinationLocation())
                .distanceKm(request.distanceKm())
                .vehicleType(request.vehicleType())
                .dispatchTime(request.dispatchTime())
                .expectedDeliveryTime(request.expectedDeliveryTime())
                .actualDeliveryTime(null)
                .weatherCondition(request.weatherCondition())
                .trafficLevel(request.trafficLevel())
                .shipmentStatus("DISPATCHED")
                .predictedDelayHours(null)
                .delayProbability(null)
                .riskScore(null)
                .createdAt(OffsetDateTime.now())
                .build();

        shipment = shipmentRepository.save(shipment);
        shipment = predictAndPersist(shipment);
        return toResponse(shipment);
    }

    @Transactional(readOnly = true)
    public Page<ShipmentListResponse> findAll(Pageable pageable) {
        return shipmentRepository.findAll(pageable).map(this::toListResponse);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse findById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + id));
        return toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse updateStatus(Long id, ShipmentUpdateStatusRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + id));
        shipment.setShipmentStatus(request.shipmentStatus());
        if ("DELIVERED".equalsIgnoreCase(request.shipmentStatus())) {
            shipment.setActualDeliveryTime(OffsetDateTime.now());
        }
        shipment = shipmentRepository.save(shipment);
        return toResponse(shipment);
    }

    @Transactional
    public ShipmentResponse predictDelay(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + id));
        shipment = predictAndPersist(shipment);
        return toResponse(shipment);
    }

    private Shipment predictAndPersist(Shipment shipment) {
        try {
            Supplier supplier = shipment.getSupplier();
            Double supplierReliability = supplier != null && supplier.getReliabilityScore() != null
                    ? supplier.getReliabilityScore()
                    : 0.5;

            DelayPredictionRequest req = new DelayPredictionRequest(
                    shipment.getDistanceKm(),
                    shipment.getTrafficLevel(),
                    shipment.getWeatherCondition(),
                    supplierReliability,
                    shipment.getVehicleType(),
                    shipment.getDispatchTime()
            );

            DelayPredictionResponse prediction = mlPredictionClient.predictDelay(req)
                    .timeout(Duration.ofSeconds(2))
                    .block();

            if (prediction != null) {
                shipment.setDelayProbability(prediction.delayProbability());
                shipment.setPredictedDelayHours(prediction.predictedDelayHours());
                applyRiskScore(shipment, supplierReliability);
                shipment = shipmentRepository.save(shipment);
                alertService.handleShipmentRiskAlert(shipment);

                Prediction p = Prediction.builder()
                        .shipment(shipment)
                        .delayProbability(prediction.delayProbability())
                        .predictedDelayHours(prediction.predictedDelayHours())
                        .predictionTime(OffsetDateTime.now())
                        .build();
                predictionRepository.save(p);
            }
        } catch (Exception ignored) {
            // Iteration 3: ML call failures should not block shipment creation
        }
        return shipment;
    }

    private void applyRiskScore(Shipment shipment, Double supplierReliabilityRaw) {
        Double delayProb = shipment.getDelayProbability();
        if (delayProb == null) {
            delayProb = 0.0;
        }

        double reliabilityNorm = supplierReliabilityRaw != null ? supplierReliabilityRaw : 0.5;
        if (reliabilityNorm > 1.0) {
            reliabilityNorm = reliabilityNorm / 100.0;
        }
        if (reliabilityNorm < 0.0) reliabilityNorm = 0.0;
        if (reliabilityNorm > 1.0) reliabilityNorm = 1.0;

        double supplierRisk = 1.0 - reliabilityNorm;

        String traffic = shipment.getTrafficLevel() != null ? shipment.getTrafficLevel().toUpperCase() : "MEDIUM";
        double trafficScore;
        switch (traffic) {
            case "LOW" -> trafficScore = 20.0;
            case "HIGH" -> trafficScore = 80.0;
            default -> trafficScore = 50.0;
        }

        Double distanceKm = shipment.getDistanceKm() != null ? shipment.getDistanceKm() : 0.0;
        double distanceNorm = Math.min(Math.max(distanceKm / 1000.0, 0.0), 1.0);
        double distanceScore = distanceNorm * 100.0;

        double delayScore = delayProb * 100.0;
        double supplierScore = supplierRisk * 100.0;

        double riskScore =
                0.4 * delayScore +
                0.3 * supplierScore +
                0.2 * trafficScore +
                0.1 * distanceScore;

        if (riskScore < 0.0) riskScore = 0.0;
        if (riskScore > 100.0) riskScore = 100.0;

        shipment.setRiskScore(riskScore);

        String level;
        if (riskScore <= 30.0) {
            level = "LOW";
        } else if (riskScore <= 70.0) {
            level = "MEDIUM";
        } else {
            level = "HIGH";
        }
        shipment.setRiskLevel(level);
    }

    private ShipmentListResponse toListResponse(Shipment s) {
        return new ShipmentListResponse(
                s.getId(),
                s.getSupplier() != null ? s.getSupplier().getSupplierName() : null,
                s.getSourceLocation(),
                s.getDestinationLocation(),
                s.getVehicleType(),
                s.getShipmentStatus(),
                s.getRiskScore(),
                s.getRiskLevel(),
                s.getDispatchTime(),
                s.getExpectedDeliveryTime()
        );
    }

    private ShipmentResponse toResponse(Shipment s) {
        return new ShipmentResponse(
                s.getId(),
                s.getSupplier() != null ? s.getSupplier().getId() : null,
                s.getSupplier() != null ? s.getSupplier().getSupplierName() : null,
                s.getSourceLocation(),
                s.getDestinationLocation(),
                s.getDistanceKm(),
                s.getVehicleType(),
                s.getDispatchTime(),
                s.getExpectedDeliveryTime(),
                s.getActualDeliveryTime(),
                s.getWeatherCondition(),
                s.getTrafficLevel(),
                s.getShipmentStatus(),
                s.getPredictedDelayHours(),
                s.getDelayProbability(),
                s.getRiskScore(),
                s.getRiskLevel(),
                s.getCreatedAt()
        );
    }
}
