package com.ssrp.repositories;

import com.ssrp.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    @Query(value = """
            select s.id,
                   s.supplier_name,
                   s.reliability_score,
                   count(sh.id) as shipment_count,
                   avg(sh.risk_score) as avg_risk
            from suppliers s
            left join shipments sh on sh.supplier_id = s.id
            group by s.id, s.supplier_name, s.reliability_score
            order by s.reliability_score desc nulls last
            limit ?1
            """, nativeQuery = true)
    List<Object[]> topSuppliersByReliability(int limit);
}

