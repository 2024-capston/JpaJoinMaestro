package org.sejong.jpajoinmaestro.core.annotations.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface AnnotationUtil<T> {
    /**
     * TODO : refactor
     * 어노테이션이 있는지 확인
     *
     * @param field           // 필드명
     * @param annotationClass // 확인할 annotation class
     * @return // true, false
     */
    Boolean isAnnotationPresent(Field field, Class<T> annotationClass);
}
