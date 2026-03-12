package com.ssrp.repositories;

import com.ssrp.entities.Shipment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {

    long countByShipmentStatusIgnoreCase(String shipmentStatus);

    @Query("select count(s) from Shipment s where upper(s.riskLevel) = upper(?1)")
    long countByRiskLevel(String riskLevel);

    @Query("select count(s) from Shipment s where s.riskScore is not null and s.riskScore >= ?1")
    long countHighRisk(double threshold);

    @Query("select avg(s.predictedDelayHours) from Shipment s where s.predictedDelayHours is not null")
    Double avgPredictedDelayHours();

    @Query(value = """
            select date_trunc('day', s.created_at) as day,
                   avg(s.predicted_delay_hours) as avg_delay
            from shipments s
            where s.predicted_delay_hours is not null
              and s.created_at >= ?1
            group by day
            order by day
            """, nativeQuery = true)
    List<Object[]> avgDelayByDaySince(OffsetDateTime since);

    @Query(value = """
            select coalesce(upper(s.shipment_status), 'UNKNOWN') as status, count(*) as cnt
            from shipments s
            group by coalesce(upper(s.shipment_status), 'UNKNOWN')
            order by cnt desc
            """, nativeQuery = true)
    List<Object[]> statusCounts();

    @Query(value = """
            select coalesce(upper(s.risk_level), 'UNKNOWN') as risk_level, count(*) as cnt
            from shipments s
            group by coalesce(upper(s.risk_level), 'UNKNOWN')
            order by cnt desc
            """, nativeQuery = true)
    List<Object[]> riskLevelCounts();
}

