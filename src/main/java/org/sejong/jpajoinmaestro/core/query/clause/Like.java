package org.sejong.jpajoinmaestro.core.query.clause;

import static org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG.LIKE;

public class Like extends Clause {
    private Object value;

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public Like with(Class<?> domainClass, String fieldName, String x) {
        initCondition(LIKE,domainClass, fieldName);
        this.value = x;
        return this;
    }

    public Object getValue() {
        return value;
    }

}
