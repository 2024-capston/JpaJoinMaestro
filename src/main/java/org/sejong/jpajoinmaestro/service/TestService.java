package org.sejong.jpajoinmaestro.service;

import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestService {
    private final UserRepository userRepository;

    public User findUserById() {
        User u = new User();
        return userRepository.findUserById(1L);
    }

}
