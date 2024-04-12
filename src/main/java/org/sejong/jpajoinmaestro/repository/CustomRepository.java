package org.sejong.jpajoinmaestro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CustomRepository<T,ID> {
    T customMethod(Class<T> domainClass, Long id);
    T customMethod(Class<T> domainClass, Class<?> myDto,Long id);
    Class<T> getOurClass();
}
