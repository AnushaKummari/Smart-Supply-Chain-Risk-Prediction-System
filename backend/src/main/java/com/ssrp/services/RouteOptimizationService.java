package com.ssrp.services;

import com.ssrp.dto.RouteOptimizationResponse;
import com.ssrp.entities.Shipment;
import com.ssrp.repositories.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteOptimizationService {

    private final ShipmentRepository shipmentRepository;

    @Transactional(readOnly = true)
    public RouteOptimizationResponse optimizeForShipment(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));

        List<RouteOptimizationResponse.RouteSuggestion> suggestions = new ArrayList<>();

        Double distanceKm = shipment.getDistanceKm();
        String traffic = safeUpper(shipment.getTrafficLevel());
        String weather = safeUpper(shipment.getWeatherCondition());
        Double predictedDelay = shipment.getPredictedDelayHours();
        Double riskScore = shipment.getRiskScore();

        if (distanceKm != null && distanceKm >= 800.0) {
            suggestions.add(new RouteOptimizationResponse.RouteSuggestion(
                    "HUB_ROUTING",
                    "Consider hub-based routing for long distance",
                    "Distance is high (" + Math.round(distanceKm) + " km). Consider splitting into legs via regional hubs to reduce disruption impact.",
                    "HIGH"
            ));
        }

        if ("HIGH".equals(traffic)) {
            suggestions.add(new RouteOptimizationResponse.RouteSuggestion(
                    "TRAFFIC_AVOIDANCE",
                    "Avoid high-traffic corridors",
                    "Traffic level is HIGH. Consider alternate highways, night driving windows, or congestion-aware routing to reduce delays.",
                    "HIGH"
            ));
        }

        if ("STORM".equals(weather) || "RAIN".equals(weather)) {
            suggestions.add(new RouteOptimizationResponse.RouteSuggestion(
                    "WEATHER_ROUTE",
                    "Adjust route for adverse weather",
                    "Weather condition is " + weather + ". Prefer safer routes and add buffer time; avoid flood-prone/low-visibility segments.",
                    "MEDIUM"
            ));
        }

        if (predictedDelay != null && predictedDelay >= 6.0) {
            suggestions.add(new RouteOptimizationResponse.RouteSuggestion(
                    "BUFFER_AND_ESCALATE",
                    "Add buffer and escalate early",
                    "Predicted delay is " + predictedDelay + " hours. Add buffer time and pre-alert stakeholders; consider expedited legs if available.",
                    "MEDIUM"
            ));
        }

        if (riskScore != null && riskScore >= 70.0) {
            suggestions.add(new RouteOptimizationResponse.RouteSuggestion(
                    "CARRIER_OPTION",
                    "Consider alternate carrier for high-risk shipments",
                    "Risk score is high. Consider switching to a more reliable carrier or adding redundancy (backup carrier).",
                    "MEDIUM"
            ));
        }

        if (suggestions.isEmpty()) {
            suggestions.add(new RouteOptimizationResponse.RouteSuggestion(
                    "NO_CHANGE",
                    "No route changes suggested",
                    "Current route signals look stable based on distance, traffic, weather, and predicted delay.",
                    "LOW"
            ));
        }

        return new RouteOptimizationResponse(
                shipment.getId(),
                distanceKm,
                shipment.getTrafficLevel(),
                shipment.getWeatherCondition(),
                shipment.getPredictedDelayHours(),
                shipment.getRiskScore(),
                suggestions
        );
    }

    private static String safeUpper(String v) {
        return v != null ? v.toUpperCase() : "";
    }
}

