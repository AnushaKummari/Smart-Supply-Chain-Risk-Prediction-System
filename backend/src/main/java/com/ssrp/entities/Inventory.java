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
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private String warehouseName;
    private Integer currentStock;
    private Integer reorderLevel;
    private Integer avgDailyUsage;
    private Integer incomingStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private Double shortageRiskScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}

