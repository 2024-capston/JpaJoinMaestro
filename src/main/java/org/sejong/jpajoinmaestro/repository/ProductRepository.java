package org.sejong.jpajoinmaestro.repository;

import org.sejong.jpajoinmaestro.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}