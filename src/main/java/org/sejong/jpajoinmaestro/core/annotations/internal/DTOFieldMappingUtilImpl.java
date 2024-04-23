package org.sejong.jpajoinmaestro.core.annotations.internal;

import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.core.annotations.spi.DTOFieldMappingUtil;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class DTOFieldMappingUtilImpl extends AnnotationUtilImpl<DTOFieldMapping> implements DTOFieldMappingUtil {
    @Override
    public DTOFieldMapping getDTOFieldMapping(Field field) {
        if(this.isAnnotationPresent(field, DTOFieldMapping.class))
            return field.getAnnotation(DTOFieldMapping.class);

        // TODO : Exception 추가
        return null;
    }

    @Override
    public Class<?> getDomainClass(Field field) {
        DTOFieldMapping annotation = this.getDTOFieldMapping(field);
        return annotation.domain();
    }

    @Override
    public String domainFieldName(Field field) {
        DTOFieldMapping annotation = this.getDTOFieldMapping(field);
        return annotation.fieldName();
    }
}
