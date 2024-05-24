package org.sejong.jpajoinmaestro.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.query.internal.JoinSelectQueryImpl;
import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.dto.CreateUserDto;
import org.sejong.jpajoinmaestro.dto.ShipmentOrder;
import org.sejong.jpajoinmaestro.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {
    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    private JoinQueryBuilder joinSelectQuery;

    public User findUserById(Long id) {
        CriteriaQuery<Object[]> joinQuery = joinSelectQuery.createJoinQuery(ShipmentOrder.class, 1L);
        return userRepository.customMethod(User.class, id);
    }
}
