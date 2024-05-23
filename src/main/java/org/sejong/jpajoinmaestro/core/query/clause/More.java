package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.FIELD_TYPE;

import java.util.Date;

import static org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG.MORE_THAN;
import static org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG.MORE_THAN_EQUAL;

public class More extends Predicate {
    private Object value;

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, int x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, String x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, Long x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, Double x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, Date x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, Boolean x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void than(Class<?> domainClass, String fieldName, Enum<?> x) {
        initCondition(MORE_THAN, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, int x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, String x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, Long x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, Double x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, Date x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, Boolean x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    /**
     * @param domainClass // A Class that has annotation @Entity
     * @param fieldName   // A field name of the domainClass
     * @param x           // A value to compare
     */
    public void thanEqual(Class<?> domainClass, String fieldName, Enum<?> x) {
        initCondition(MORE_THAN_EQUAL, domainClass, fieldName);
        this.value = x;
    }

    public Object getValue() {
        return this.value;
    }

    public FIELD_TYPE getValueType() {
        return getValueType(this.value);
    }
}
