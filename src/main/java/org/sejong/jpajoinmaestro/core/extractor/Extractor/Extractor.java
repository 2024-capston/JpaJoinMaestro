package org.sejong.jpajoinmaestro.core.extractor.Extractor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.sejong.jpajoinmaestro.core.extractor.domain.ExtractedIndex;
import org.sejong.jpajoinmaestro.core.extractor.spi.IExtractor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Extractor implements IExtractor {
    private EntityManager em;

    public Extractor(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<ExtractedIndex> getEntityIndexes(Class<?> domainClass) {
        List<ExtractedIndex> results = new ArrayList<>();
        // add primary key
        results.add(extractPrimaryKey(domainClass));

        // add indexes defined by @Table annotation
        Table annotation = domainClass.getAnnotation(Table.class);
        if(annotation != null) {
            Index[] indexes = annotation.indexes();
            for(Index index: indexes) {
                results.add(new ExtractedIndex(index.name(), index.unique(), String.join(", ", index.columnList()), false));
            }
        }
        return results;
    }

    private ExtractedIndex extractPrimaryKey(Class<?> domainClass) {
        Field[] fields = domainClass.getDeclaredFields();
        for (Field field: fields) {
            if(field.isAnnotationPresent(Id.class)) {
                return new ExtractedIndex(null, true, field.getName(), true);
            }
        }
        // TODO : Not Found Primary Key Exception
        return null;
    }
}
