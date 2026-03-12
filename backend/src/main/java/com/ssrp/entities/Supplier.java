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
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    private String contactEmail;
    private String contactPhone;
    private Double averageLeadTime;
    private Double onTimeDeliveryRate;
    private Double defectRate;
    private Double cancellationRate;
    private Double reliabilityScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

