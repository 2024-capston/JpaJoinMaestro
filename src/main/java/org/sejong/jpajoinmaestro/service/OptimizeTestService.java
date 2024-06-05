package org.sejong.jpajoinmaestro.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
import org.sejong.jpajoinmaestro.domain.*;
import org.sejong.jpajoinmaestro.dto.CreateUserDto;
import org.sejong.jpajoinmaestro.dto.MyOrder;
import org.sejong.jpajoinmaestro.dto.ShipmentOrder;
import org.sejong.jpajoinmaestro.repository.ShipmentRepository;
import org.sejong.jpajoinmaestro.repository.UserRepository;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptimizeTestService {
    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;
    private final JoinQueryBuilder joinQueryBuilder;
    private final ShipmentRepository shipmentRepository;
    private final JPAQueryFactory queryFactory;

    public User findUserByIdOpt(Long id) {
        //CriteriaQuery<Object[]> joinQuery = joinSelectQuery.createJoinQuery(ShipmentOrder.class, 1L);
        return userRepository.customMethod(User.class, id);
    }

    public List<?> customJoinTest(){
        ClauseBuilder pb = new ClauseBuilder()
                .where(new Equal().to(Shipment.class, "shipmentStatus", "IN_TRANSIT"))
                .andWhere(new Equal().to(User.class, "id", 1));

        List<ShipmentOrder> results = joinQueryBuilder.createJoinQuery(ShipmentOrder.class, pb);
        return results;
    }

    public List<ShipmentOrder> getAllShipmentOrders() {
        List<Shipment> shipments = shipmentRepository.findAll();

        return shipments.stream()
                .filter(shipment -> {
                    Long userId = shipment.getOrders().getUser().getId();
                    return userId == 1;
                })
                .map(shipment -> {
                    Orders order = shipment.getOrders();
                    return new ShipmentOrder(
                            shipment.getShipmentStatus(),
                            order.getId(),
                            order.getUser().getId(),
                            shipment.getId()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<?> getAllShipmentOrdersByQueryDsl() {
        QShipment shipment = QShipment.shipment;
        QOrders orders = QOrders.orders;
        QUser user = QUser.user;
        // generate List<ShipmentOrder> when userId value is in between 1, 100 using QueryDSL
        List<Shipment> shipments = queryFactory.selectFrom(shipment)
                .leftJoin(shipment.orders, orders)
                .innerJoin(orders.user, user)
                .where(user.id.eq(1L))
                .fetch();

        // map List<Shipment> to List<ShipmentOrder>
        List<ShipmentOrder> shipmentOrders = shipments.stream()
                .map(s -> {
                    Orders order = s.getOrders();
                    return new ShipmentOrder(
                            s.getShipmentStatus(),
                            order.getId(),
                            order.getUser().getId(),
                            s.getId()
                    );
                })
                .toList();
        return shipmentOrders;
    }

    public List<MyOrder> getMyOrder() {
        ClauseBuilder pb = new ClauseBuilder()
                .where(new Equal().to(User.class, "id", 1))
                .andWhere(new Equal().to(Orders.class, "user_id", 1))
                .andWhere(new Equal().to(OrderDetail.class, "orders_id", 1));
        List<MyOrder> results = joinQueryBuilder.createJoinQuery(MyOrder.class, pb);
        return results;
    }
}
