package com.ssrp.services;

import com.ssrp.dto.RecommendationResponse;
import com.ssrp.entities.Shipment;
import com.ssrp.entities.Supplier;
import com.ssrp.repositories.ShipmentRepository;
import com.ssrp.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final ShipmentRepository shipmentRepository;
    private final SupplierRepository supplierRepository;

    @Transactional(readOnly = true)
    public RecommendationResponse recommendForShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));

        Supplier supplier = shipment.getSupplier();
        Double supplierReliability = supplier != null ? supplier.getReliabilityScore() : null;
        double reliabilityNorm = normalizeReliability(supplierReliability);

        List<RecommendationResponse.RecommendationItem> items = new ArrayList<>();

        Double riskScore = shipment.getRiskScore();
        Double predictedDelayHours = shipment.getPredictedDelayHours();
        Double delayProbability = shipment.getDelayProbability();

        // Alternate supplier suggestion
        if (riskScore != null && riskScore >= 70.0 && reliabilityNorm < 0.6) {
            Supplier alt = findAlternateSupplier(supplier);
            if (alt != null) {
                items.add(new RecommendationResponse.RecommendationItem(
                        "ALTERNATE_SUPPLIER",
                        "Consider alternate supplier",
                        "Current supplier reliability is low. Suggested alternate supplier: " + alt.getSupplierName(),
                        "HIGH"
                ));
            } else {
                items.add(new RecommendationResponse.RecommendationItem(
                        "ALTERNATE_SUPPLIER",
                        "Consider alternate supplier",
                        "Current supplier reliability is low. No alternate suppliers available yet (add more suppliers to enable suggestions).",
                        "MEDIUM"
                ));
            }
        }

        // Alternate vehicle suggestion (rule-based)
        String traffic = safeUpper(shipment.getTrafficLevel());
        if ("HIGH".equals(traffic) && predictedDelayHours != null && predictedDelayHours >= 4.0) {
            items.add(new RecommendationResponse.RecommendationItem(
                    "ALTERNATE_VEHICLE",
                    "Consider alternate vehicle type",
                    "Traffic is high and predicted delay is significant. Consider using a faster/more agile vehicle (e.g., VAN) if feasible.",
                    "MEDIUM"
            ));
        }

        // Dispatch time change suggestion
        OffsetDateTime dispatchTime = shipment.getDispatchTime();
        if (dispatchTime != null && (traffic.equals("HIGH") || (delayProbability != null && delayProbability >= 0.7))) {
            int hour = dispatchTime.getHour();
            if (isPeakHour(hour)) {
                items.add(new RecommendationResponse.RecommendationItem(
                        "DISPATCH_TIME_CHANGE",
                        "Adjust dispatch time to avoid peak hours",
                        "Dispatch is during peak hours (" + hour + ":00). Consider dispatching earlier/later to reduce congestion-related delays.",
                        "MEDIUM"
                ));
            }
        }

        // Route improvement suggestion if distance is high
        Double distanceKm = shipment.getDistanceKm();
        if (distanceKm != null && distanceKm >= 800.0) {
            items.add(new RecommendationResponse.RecommendationItem(
                    "ROUTE_IMPROVEMENT",
                    "Optimize long-distance route",
                    "Route distance is high (" + Math.round(distanceKm) + " km). Consider multi-leg routing, regional hubs, or alternative carriers to reduce risk.",
                    "MEDIUM"
            ));
        }

        // Weather-based recommendation
        String weather = safeUpper(shipment.getWeatherCondition());
        if ("STORM".equals(weather) || "RAIN".equals(weather)) {
            items.add(new RecommendationResponse.RecommendationItem(
                    "WEATHER_MITIGATION",
                    "Mitigate weather-related delays",
                    "Adverse weather (" + weather + ") detected. Consider buffer time, safer routes, and proactive customer notifications.",
                    "LOW"
            ));
        }

        // General recommendation when predictions missing
        if (predictedDelayHours == null || delayProbability == null) {
            items.add(new RecommendationResponse.RecommendationItem(
                    "RUN_PREDICTION",
                    "Run delay prediction",
                    "Delay prediction is not available yet. Run the delay prediction to generate risk-aware recommendations.",
                    "LOW"
            ));
        }

        return new RecommendationResponse(
                shipment.getId(),
                shipment.getRiskLevel(),
                shipment.getRiskScore(),
                shipment.getPredictedDelayHours(),
                shipment.getDelayProbability(),
                items
        );
    }

    private Supplier findAlternateSupplier(Supplier current) {
        // Simple strategy: highest reliability supplier that's not the current one.
        // Uses the existing supplier table; reliabilityScore may be null.
        return supplierRepository.findAll().stream()
                .filter(s -> current == null || !s.getId().equals(current.getId()))
                .max(Comparator.comparingDouble(s -> normalizeReliability(s.getReliabilityScore())))
                .orElse(null);
    }

    private static boolean isPeakHour(int hour) {
        return (hour >= 7 && hour <= 10) || (hour >= 16 && hour <= 19);
    }

    private static String safeUpper(String v) {
        return v != null ? v.toUpperCase() : "";
    }

    private static double normalizeReliability(Double reliability) {
        if (reliability == null) return 0.5;
        double r = reliability;
        if (r > 1.0) r = r / 100.0;
        if (r < 0.0) r = 0.0;
        if (r > 1.0) r = 1.0;
        return r;
    }
}

