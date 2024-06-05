package org.sejong.jpajoinmaestro.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ShipmentRepositoryImpl implements ShipmentRepositoryCustom {
    private final JPAQueryFactory queryFactory;
}
