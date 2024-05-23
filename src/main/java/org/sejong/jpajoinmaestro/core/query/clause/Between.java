package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;
import org.sejong.jpajoinmaestro.core.query.constants.FIELD_TYPE;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class Between extends Predicate {
    private Object start;
    private Object end;

    public Between between(Class<?> domainClass, String fieldName, int start, int end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, String start, String end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, Long start, Long end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, Double start, Double end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, java.util.Date start, java.util.Date end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, java.sql.Date start, java.sql.Date end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, Timestamp start, Timestamp end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, Time start, Time end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
        return this;
    }

    public Between between(Class<?> domainClass, String fieldName, LocalDate start, LocalDate end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
    }

    public Between between(Class<?> domainClass, String fieldName, LocalDateTime start, LocalDateTime end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
    }

    public Between between(Class<?> domainClass, String fieldName, LocalTime start, LocalTime end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
    }

    public Between between(Class<?> domainClass, String fieldName, OffsetDateTime start, OffsetDateTime end) {
        initCondition(CONDITION_FLAG.BETWEEN, domainClass, fieldName);
        this.start = start;
        this.end = end;
    }


    public Object getStart() {
        return start;
    }

    public Object getEnd() {
        return end;
    }

    public FIELD_TYPE getStartType() {
        return getValueType(start);
    }

    public FIELD_TYPE getEndType() {
        return getValueType(end);
    }
}
