package org.sejong.jpajoinmaestro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Deprecated
 * @param <T>
 * @param <ID>
 */
public interface CustomRepository<T,ID> {
    T customMethod(Class<T> domainClass, Long id);
    T customMethod(Class<T> domainClass, Class<?> myDto,Long id);
    Class<T> getOurClass();
    //<S extends T> S dualJoinMethod(Class<T> owner, Class<?> slave);
    // TODO : Mapping DTO with Entitiy
    // 해결된 거 : Class 만 받으면 Class Properties 는 해결
    // 해결할 거 : 각 Class(DTO) 의 Properties 가 어떤 Entity 의 어떤 Properties 와 매핑되는지 (설계 + 구현)
    // TODO : Mapping Result of Query with DTO
}
