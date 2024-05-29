package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;

import java.util.HashMap;
import java.util.Queue;

import static org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION.*;

public class ClauseBuilder {
    private Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates;
    private Queue<HashMap<String, String>> orderBy;
    private Queue<HashMap<String, String>> groupBy;
    private Integer skip;
    private Integer take;

    public ClauseBuilder() {}

    public ClauseBuilder where(Clause clause) {
        predicates.offer(new HashMap<>() {{
            put(FIRST, clause);
        }});
        return this;
    }


    public ClauseBuilder andWhere(Clause clause) {
        predicates.offer(new HashMap<>() {{
            put(AND, clause);
        }});
        return this;
    }

    public ClauseBuilder orWhere(Clause clause) {
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

    public ClauseBuilder orderBy(String field, String order) {
        orderBy.offer(new HashMap<>() {{
            put(field, order);
        }});
        return this;
    }

    public ClauseBuilder groupBy(String field, String order) {
        groupBy.offer(new HashMap<>() {{
            put(field, order);
        }});
        return this;
    }

    public ClauseBuilder skip(Integer skip) {
        this.skip = skip;
        return this;
    }

    public ClauseBuilder take(Integer take) {
        this.take = take;
        return this;
    }

    public Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> getPredicates() {
        return predicates;
    }

    public Queue<HashMap<String, String>> getOrderBy() {
        return orderBy;
    }

    public Queue<HashMap<String, String>> getGroupBy() {
        return groupBy;
    }

    public Integer getSkip() {
        return skip;
    }

    public Integer getTake() {
        return take;
    }

    public ClauseBuilder build() {
        return this;
    }
}
