package com.ssrp.services;

import com.ssrp.dto.AlertResponse;
import com.ssrp.entities.Alert;
import com.ssrp.entities.Shipment;
import com.ssrp.repositories.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public Page<AlertResponse> getAlerts(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Alert> page = alertRepository.findAll(sorted);
        List<AlertResponse> content = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(content, sorted, page.getTotalElements());
    }

    @Transactional
    public void handleShipmentRiskAlert(Shipment shipment) {
        Double riskScore = shipment.getRiskScore();
        if (riskScore == null) {
            return;
        }

        String severity;
        if (riskScore >= 90.0) {
            severity = "CRITICAL";
        } else if (riskScore >= 70.0) {
            severity = "HIGH";
        } else if (riskScore >= 50.0) {
            severity = "MEDIUM";
        } else if (riskScore >= 30.0) {
            severity = "LOW";
        } else {
            return;
        }

        if (alertRepository.existsByShipmentAndSeverity(shipment, severity)) {
            return;
        }

        String alertType = "SHIPMENT_RISK";
        String message = String.format(
                "Shipment %d risk score %.1f (%s)",
                shipment.getId(),
                riskScore,
                shipment.getRiskLevel()
        );

        Alert alert = Alert.builder()
                .alertType(alertType)
                .alertMessage(message)
                .severity(severity)
                .shipment(shipment)
                .supplier(shipment.getSupplier())
                .inventory(null)
                .createdAt(OffsetDateTime.now())
                .build();

        Alert saved = alertRepository.save(alert);

        // Real-time notification broadcast (Iteration 10 feature)
        try {
            messagingTemplate.convertAndSend("/topic/alerts", toResponse(saved));
        } catch (Exception ignored) {
            // Don't break alert persistence if WS broker is unavailable
        }
    }

    private AlertResponse toResponse(Alert alert) {
        Long shipmentId = alert.getShipment() != null ? alert.getShipment().getId() : null;
        Long supplierId = alert.getSupplier() != null ? alert.getSupplier().getId() : null;
        Long inventoryId = alert.getInventory() != null ? alert.getInventory().getId() : null;

        return new AlertResponse(
                alert.getId(),
                alert.getAlertType(),
                alert.getAlertMessage(),
                alert.getSeverity(),
                shipmentId,
                supplierId,
                inventoryId,
                alert.getCreatedAt()
        );
    }
}

