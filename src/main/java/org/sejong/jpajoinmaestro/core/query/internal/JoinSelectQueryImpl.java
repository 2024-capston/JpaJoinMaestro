package org.sejong.jpajoinmaestro.core.query.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.annotations.spi.DTOFieldMappingUtil;
import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JoinSelectQueryImpl implements JoinQueryBuilder {
    @PersistenceContext
    private EntityManager entityManager;

    private final DTOFieldMappingUtil dtoFieldMapping;

    @Override
    public <T> CriteriaQuery<Object[]> createJoinQuery(Class<T> dtoClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Map<Class<?>, Root<?>> roots = new HashMap<>();

        List<Selection<?>> selections = new ArrayList<>();
        for(Field field : dtoClass.getDeclaredFields()) {
            Class<?> domainClass = dtoFieldMapping.getDomainClass(field);
            String domainFieldName = dtoFieldMapping.domainFieldName(field);
            System.out.println("domainClass: "  + domainClass);
            // Add to the selection if the domain class is part of the query
            roots.put(domainClass, cq.from(domainClass));
            selections.add(roots.get(domainClass).get(domainFieldName));
        }

        // Build the select clause with dynamic fields
        cq.multiselect(selections);
        return cq;
    }
}
