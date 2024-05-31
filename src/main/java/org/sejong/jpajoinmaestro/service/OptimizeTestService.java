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
import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
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
    private final JoinQueryBuilder joinQueryBuilder;

    public User findUserByIdOpt(Long id) {
        //CriteriaQuery<Object[]> joinQuery = joinSelectQuery.createJoinQuery(ShipmentOrder.class, 1L);
        return userRepository.customMethod(User.class, id);
    }

    public void testMethod() {
        ClauseBuilder pb = new ClauseBuilder()
                .where(new More().than(Shipment.class, "shipmentStatus", "TRANSIT"))
                .andWhere(new Equal().to(Orders.class, "userId", 1))
                .andWhere(new Like().with(Orders.class, "status", "DONE%"))
                .andWhere(new Between().between(Shipment.class, "id", 1, 10));
        WhereClauseOptimizer testOptimizer = new WhereClauseOptimizerImpl();
        PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Clause>> testClauseQueue =  testOptimizer.getOptimizedWhereClause(ShipmentOrder.class, pb.getPredicates());

        /* 테스트 출력 */
        for(HashMap<PREDICATE_CONJUNCTION, Clause> map : testClauseQueue) {
            for(Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : map.entrySet()) {
                PREDICATE_CONJUNCTION conjunction =  entry.getKey();
                Clause predicate = entry.getValue();
                System.out.println("[Clauses Optimize Test] "
                        + conjunction.name() + "::"
                        + predicate.getDomainClass() + "."
                        + predicate.getFieldName() + " "
                        + predicate.getFlag() + " "
                        /*다운캐스팅해서 getValue*/);
            }
        }
    }

    public void customJoinTest(){
        ClauseBuilder pb = new ClauseBuilder()
                .where(new Equal().to(Shipment.class, "shipmentStatus", "IN_TRANSIT"))
                .andWhere(new Equal().to(User.class, "id", 1))
//                .andWhere(new Like().with(Orders.class, "status", "DONE%"))
                .andWhere(new Between().between(Shipment.class, "id", 1, 10));

        List<ShipmentOrder> results = joinQueryBuilder.createJoinQuery(ShipmentOrder.class, pb);
        for(ShipmentOrder result : results){
            System.out.println(result);
        }
    }
}
