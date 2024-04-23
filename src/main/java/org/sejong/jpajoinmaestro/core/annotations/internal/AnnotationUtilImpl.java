package org.sejong.jpajoinmaestro.core.annotations.internal;

import org.sejong.jpajoinmaestro.core.annotations.spi.AnnotationUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class AnnotationUtilImpl<T extends Annotation> implements AnnotationUtil<T> {

    @Override
    public Boolean isAnnotationPresent(Field field, Class<T> annotationClass) {
        return field.isAnnotationPresent(annotationClass);
    }
}
