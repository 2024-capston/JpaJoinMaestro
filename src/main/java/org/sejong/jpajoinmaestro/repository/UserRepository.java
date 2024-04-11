package org.sejong.jpajoinmaestro.repository;

import org.sejong.jpajoinmaestro.domain.User;
import org.sejong.jpajoinmaestro.test.spi.CustomizedUserRepository;
import org.sejong.jpajoinmaestro.test.spi.JoinMaestroRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends CrudRepository<User, Long>, CustomizedUserRepository {
    User findUserById(Long id);
}