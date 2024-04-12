package org.sejong.jpajoinmaestro.repository;

import org.sejong.jpajoinmaestro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, CustomRepository<User,Long> {
    User findByEmail(String name);
}
