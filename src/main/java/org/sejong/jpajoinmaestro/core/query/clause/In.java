package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;
import org.sejong.jpajoinmaestro.core.query.constants.FIELD_TYPE;

public class In extends Predicate {
    private Object[] collections;

    public In in(Class<?> domainClass, String fieldName, Object... collections) {
        initCondition(CONDITION_FLAG.IN, domainClass, fieldName);
        this.collections = collections;
        return this;
    }

    public Object[] getCollections() {
        return collections;
    }

    public FIELD_TYPE getCollectionType() {
        if (collections.length > 0) {
            if (collections[0] instanceof Integer) {
                return FIELD_TYPE.INTEGER;
            } else if (collections[0] instanceof String) {
                return FIELD_TYPE.STRING;
            } else if (collections[0] instanceof Long) {
                return FIELD_TYPE.LONG;
            } else if (collections[0] instanceof Double) {
                return FIELD_TYPE.DOUBLE;
            } else if (collections[0] instanceof java.util.Date) {
                return FIELD_TYPE.DATE;
            }
        }
        return FIELD_TYPE.STRING;
    }
}
