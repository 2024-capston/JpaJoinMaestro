package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.SORTING;

public class Sorting {
    String field;
    Class<?> domainClass;
    SORTING sorting;

    public String getField() {
        return field;
    }

    public Class<?> getDomainClass() {
        return domainClass;
    }

    public SORTING getSorting() {
        return sorting;
    }
}
