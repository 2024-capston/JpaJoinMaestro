package org.sejong.jpajoinmaestro.core.optimizer.spi;

import jakarta.persistence.criteria.CriteriaQuery;
import org.sejong.jpajoinmaestro.core.query.clause.Predicate;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public interface WhereClauseOptimizer {
	PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Predicate>> getOptimizedWhereClause(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates);
}
