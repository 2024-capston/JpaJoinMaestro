package org.sejong.jpajoinmaestro.core.optimizer.spi;

import org.sejong.jpajoinmaestro.core.query.clause.Clause;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;

import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public interface WhereClauseOptimizer {
	PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Clause>> getOptimizedWhereClause(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates);
}
