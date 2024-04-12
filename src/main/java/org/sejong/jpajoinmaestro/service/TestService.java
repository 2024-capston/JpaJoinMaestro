package org.sejong.jpajoinmaestro.service;

import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.dto.CreateUserDto;
import org.sejong.jpajoinmaestro.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final UserRepository userRepository;

    public User findUserById(Long id) {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setPassword("1234");
        createUserDto.setUsername("qwer");
        userRepository.customMethod(User.class, createUserDto.getClass(), id);
        return userRepository.customMethod(User.class, id);
    }
}
