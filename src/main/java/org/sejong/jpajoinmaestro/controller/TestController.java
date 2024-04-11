package org.sejong.jpajoinmaestro.controller;

import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

//    @GetMapping("/users")
//    public List<User> getAllUsers() {
//        return this.testService.findAllUsers();
//    }

    @GetMapping("/user")
    public User getUser() {
        return this.testService.findUserById();
    }
}
