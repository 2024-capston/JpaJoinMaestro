package org.sejong.jpajoinmaestro.repository;

import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
import org.sejong.jpajoinmaestro.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long>, CustomRepository<User,Long>{
//    User findByEmail(String name);
    User customMethod(Class<User> userClass, Long id);
}
