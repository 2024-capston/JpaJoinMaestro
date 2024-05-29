package org.sejong.jpajoinmaestro.controller;

import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.service.OptimizeTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OptimizeTestController {
    private final OptimizeTestService optimizeTestService;

    @GetMapping("/optimize/test")
    public void optimizeTest() {
        optimizeTestService.testMethod();
    }

    @GetMapping("/customJoinTest")
    public void customJoinTest() { optimizeTestService.customJoinTest();}
}
