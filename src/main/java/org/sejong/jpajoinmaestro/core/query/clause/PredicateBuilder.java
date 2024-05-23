package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

import static org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION.*;
import static org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION.AND;

public class PredicateBuilder {
    private Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates;

    public PredicateBuilder(Predicate initialPredicate) {
        predicates = new ArrayDeque<>();
        predicates.offer(new HashMap<>() {{
            put(FIRST, initialPredicate);
        }});
    }

    public void and(Predicate predicate) {
        predicates.offer(new HashMap<>() {{
            put(AND, predicate);
        }});
    }

    public void or(Predicate predicate) {
        predicates.offer(new HashMap<>() {{
            put(OR, predicate);
        }});
    }

    public void not(Predicate predicate) {
        predicates.offer(new HashMap<>() {{
            put(NOT, predicate);
        }});
    }

    public Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> build() {
        return predicates;
    }
}
