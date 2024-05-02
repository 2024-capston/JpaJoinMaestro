package org.sejong.jpajoinmaestro.core.query.spi;

import jakarta.persistence.criteria.CriteriaQuery;

public interface JoinQueryBuilder {
    /**
     * 2개의 Entity 클래스를 조인하는 CriteriaQuery를 생성한다.
     * @param dtoClass
     * @return
     * @param <T>
     */
    <T> CriteriaQuery<Object[]> createJoinQuery(Class<T> dtoClass, Long id);
}
