package org.sejong.jpajoinmaestro.core.query.clause;

import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;
import org.sejong.jpajoinmaestro.core.query.constants.FIELD_TYPE;

abstract class Clause {
    protected CONDITION_FLAG flag;
    protected Class<?> domainClass;
    protected String fieldName;

    protected void initCondition(CONDITION_FLAG flag, Class<?> domainClass, String fieldName) {
        this.flag = flag;
        this.domainClass = domainClass;
        this.fieldName = fieldName;
    }

    public CONDITION_FLAG getFlag() {
        return this.flag;
    }

    public Class<?> getDomainClass() {
        return this.domainClass;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    // TODO : ENUM 갯수랑 값 가져오기 
    protected FIELD_TYPE getValueType(Object value) {
        if (value instanceof Enum<?>) {
            return FIELD_TYPE.ENUM;
        }

        return switch (value.getClass().getSimpleName()) {
            case "String" -> FIELD_TYPE.STRING;
            case "Integer" -> FIELD_TYPE.INTEGER;
            case "Long" -> FIELD_TYPE.LONG;
            case "Double" -> FIELD_TYPE.DOUBLE;
            case "Float" -> FIELD_TYPE.FLOAT;
            case "Date" -> FIELD_TYPE.DATE;
            case "Boolean" -> FIELD_TYPE.BOOLEAN;
            default -> FIELD_TYPE.OBJECT;
        };
    }
}
