package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.SORTING;

public class Desc extends Sorting {
    private final String field;
    private final Class<?> domainClass;
    private final SORTING sorting;

    public Desc(Class<?> domainClass, String field) {
        this.domainClass = domainClass;
        this.field = field;
        this.sorting = SORTING.ASC;
    }

    public Desc(Class<?> domainClass, String field, SORTING sorting) {
        this.domainClass = domainClass;
        this.field = field;
        this.sorting = sorting;
    }

}
