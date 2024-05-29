package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.SORTING;

public class Asc extends Sorting {
    public Asc(Class<?> domainClass, String field) {
        this.domainClass = domainClass;
        this.field = field;
        this.sorting = SORTING.ASC;
    }

    public Asc(Class<?> domainClass, String field, SORTING sorting) {
        this.domainClass = domainClass;
        this.field = field;
        this.sorting = sorting;
    }
}
