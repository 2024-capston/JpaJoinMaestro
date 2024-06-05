package org.sejong.jpajoinmaestro.controller;

import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.dto.MyOrder;
import org.sejong.jpajoinmaestro.service.OptimizeTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OptimizeTestController {
    private final OptimizeTestService optimizeTestService;

    @GetMapping("/customJoinTest")
    public List<?> customJoinTest() { return optimizeTestService.customJoinTest();}

    @GetMapping("/mockJpa")
    public List<?> customJoinTest2() { return optimizeTestService.getAllShipmentOrders();}

    @GetMapping("/querydsl")
    public List<?> customJoinTest3() { return optimizeTestService.getAllShipmentOrdersByQueryDsl();}

    @GetMapping("/myorder")
    public List<MyOrder> getMyOrder() { return optimizeTestService.getMyOrder();}
}
