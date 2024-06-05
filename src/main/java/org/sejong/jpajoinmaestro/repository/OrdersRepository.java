package org.sejong.jpajoinmaestro.repository;

import org.sejong.jpajoinmaestro.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Long>{
}
