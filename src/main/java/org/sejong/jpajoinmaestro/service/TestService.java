package org.sejong.jpajoinmaestro.service;

import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final UserRepository userRepository;

    public User findUserById() {
        userRepository.customMethod();
        return userRepository.findByEmail("3OFaRsqghLDbNo7Cvxle");
    }
}
