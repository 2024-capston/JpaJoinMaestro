package org.sejong.jpajoinmaestro.core.optimizer.spi;

import jakarta.persistence.criteria.CriteriaQuery;

public interface WhereClauseOptimizer {
	<T> CriteriaQuery<T> getOptimizedWhereClause(Class<?> dtoClass, Class<?> clause);
}
