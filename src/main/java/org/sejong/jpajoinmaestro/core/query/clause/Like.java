package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;

import static org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG.LIKE;

public class Like extends Predicate{
    private Object value;

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void with(Class<?> domainClass, String fieldName, String x) {
        initCondition(LIKE,domainClass, fieldName);
        this.value = x;
    }

    public Object getValue() {
        return value;
    }

}
