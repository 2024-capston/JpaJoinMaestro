package org.sejong.jpajoinmaestro.repository;

import org.sejong.jpajoinmaestro.domain.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment,Long> {
}
