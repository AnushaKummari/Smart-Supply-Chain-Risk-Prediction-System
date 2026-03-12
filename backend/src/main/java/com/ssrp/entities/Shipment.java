package com.ssrp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String sourceLocation;
    private String destinationLocation;
    private Double distanceKm;
    private String vehicleType;

    private OffsetDateTime dispatchTime;
    private OffsetDateTime expectedDeliveryTime;
    private OffsetDateTime actualDeliveryTime;

    private String weatherCondition;
    private String trafficLevel;
    private String shipmentStatus;

    private Double predictedDelayHours;
    private Double delayProbability;
    private Double riskScore;
    private String riskLevel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

