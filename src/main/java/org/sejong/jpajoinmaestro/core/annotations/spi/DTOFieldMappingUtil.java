package org.sejong.jpajoinmaestro.core.annotations.spi;

import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;

import java.lang.reflect.Field;

public interface DTOFieldMappingUtil {
    /**
     * Get DTOFieldMapping 어노테이션
     * @param field // DTO 에 있는 Field
     * @return
     */
    DTOFieldMapping getDTOFieldMapping(Field field);

    /**
     * DTO Field 에서 Domain Class 추출
     * @param field
     * @return // domain Class 추출
     */
    Class<?> getDomainClass(Field field);

    /**
     * DTO Field 에서 Domain Field Name 추출
     * @param field
     * @return // domain Field Name 추출
     */
    String domainFieldName(Field field);
}
