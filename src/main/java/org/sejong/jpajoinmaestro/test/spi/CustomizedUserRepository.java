package org.sejong.jpajoinmaestro.test.spi;

import org.sejong.jpajoinmaestro.domain.User;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomizedUserRepository {
    void someCustomMethod(User user);
}
