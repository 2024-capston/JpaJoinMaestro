package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

import static org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION.*;
import static org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION.AND;

public class ClauseBuilder {
    private Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates;

    public ClauseBuilder(Clause initialClause) {
        predicates = new ArrayDeque<>();
        predicates.offer(new HashMap<>() {{
            put(FIRST, initialClause);
        }});
    }

    public ClauseBuilder and(Clause clause) {
        predicates.offer(new HashMap<>() {{
            put(AND, clause);
        }});
        return this;
    }

    public ClauseBuilder or(Clause clause) {
        predicates.offer(new HashMap<>() {{
            put(OR, clause);
        }});
        return this; 
    }

    public ClauseBuilder not(Clause clause) {
        predicates.offer(new HashMap<>() {{
            put(NOT, clause);
        }});
        return this;
    }

    public Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> getPredicates() {
        return predicates;
    }

    public ClauseBuilder build() {
        return this;
    }
}
