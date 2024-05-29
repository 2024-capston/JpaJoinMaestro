package org.sejong.jpajoinmaestro.core.query.clause;

public class Groupping {
    private Class<?> domainClass;
    private String field;

    public Groupping(Class<?> domainClass, String field) {
        this.domainClass = domainClass;
        this.field = field;
    }
}
