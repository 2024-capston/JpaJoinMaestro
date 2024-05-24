package org.sejong.jpajoinmaestro.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.optimizer.internal.WhereClauseOptimizerImpl;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.core.query.clause.*;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;
import org.sejong.jpajoinmaestro.core.query.internal.JoinSelectQueryImpl;
import org.sejong.jpajoinmaestro.domain.Orders;
import org.sejong.jpajoinmaestro.domain.Shipment;
import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.dto.CreateUserDto;
import org.sejong.jpajoinmaestro.dto.ShipmentOrder;
import org.sejong.jpajoinmaestro.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class OptimizeTestService {
    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    //private final JoinSelectQueryImpl joinSelectQuery;

    public User findUserByIdOpt(Long id) {
        //CriteriaQuery<Object[]> joinQuery = joinSelectQuery.createJoinQuery(ShipmentOrder.class, 1L);
        return userRepository.customMethod(User.class, id);
    }
    public void testMethod() {
        PredicateBuilder pb = new PredicateBuilder(
                new More().than(Shipment.class, "shipmentStatus", "TRANSIT"))
                .and(new Equal().to(Orders.class, "userId", 1))
                .and(new Like().with(Orders.class, "status", "DONE%"))
                .and(new Between().between(Shipment.class, "id", 1, 10));
        WhereClauseOptimizer testOptimizer = new WhereClauseOptimizerImpl();
        PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Predicate>> testPredicateQueue =  testOptimizer.getOptimizedWhereClause(ShipmentOrder.class, pb.getPredicates());

        /* 테스트 출력 */
        for(HashMap<PREDICATE_CONJUNCTION, Predicate> map : testPredicateQueue) {
            for(Map.Entry<PREDICATE_CONJUNCTION, Predicate> entry : map.entrySet()) {
                PREDICATE_CONJUNCTION conjunction =  entry.getKey();
                Predicate predicate = entry.getValue();
                System.out.println("[Predicates Optimize Test] "
                        + conjunction.name() + "::"
                        + predicate.getDomainClass() + "."
                        + predicate.getFieldName() + " "
                        + predicate.getFlag() + " "
                        /*다운캐스팅해서 getValue*/);
            }
        }
    }
}
