package org.sejong.jpajoinmaestro.test.spi;

import org.sejong.jpajoinmaestro.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class CustomizedUserRepositoryImpl implements CustomizedUserRepository {
    @Override
    public void someCustomMethod(User user) {
        System.out.println("CustomizedUserRepositoryImpl.someCustomMethod");
    }
}
