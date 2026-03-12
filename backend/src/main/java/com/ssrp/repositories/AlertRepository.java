package com.ssrp.repositories;

import com.ssrp.entities.Alert;
import com.ssrp.entities.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    boolean existsByShipmentAndSeverity(Shipment shipment, String severity);
}
