package org.sejong.jpajoinmaestro.core.query.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.sejong.jpajoinmaestro.core.annotations.spi.DTOFieldMappingUtil;
import org.sejong.jpajoinmaestro.core.query.clause.ClauseBuilder;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;
import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
import org.sejong.jpajoinmaestro.core.query.clause.Clause;

import java.lang.reflect.Field;
import java.util.*;

public class JoinSelectQueryImpl implements JoinQueryBuilder {
    private final EntityManager entityManager;
    private final DTOFieldMappingUtil dtoFieldMapping;

    public JoinSelectQueryImpl(EntityManager entityManager, DTOFieldMappingUtil dtoFieldMapping) {
        this.entityManager = entityManager;
        this.dtoFieldMapping = dtoFieldMapping;
    }

    @Override
    public <T> CriteriaQuery<Object[]> createJoinQuery(Class<T> dtoClass, Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Boolean isSet = false;
        Map<Class<?>, Root<?>> roots = new HashMap<>();

        List<Selection<?>> selections = new ArrayList<>();
        for(Field field : dtoClass.getDeclaredFields()) {
            Class<?> domainClass = dtoFieldMapping.getDomainClass(field);
            String domainFieldName = dtoFieldMapping.domainFieldName(field);
            // Add to the selection if the domain class is part of the query
            roots.put(domainClass, cq.from(domainClass));
            selections.add(roots.get(domainClass).get(domainFieldName).alias(field.getName()));
            if(!isSet) {
                isSet = true;
                cq.where(cb.equal(roots.get(domainClass).get("id"), id));
            }
        }
//        // Build the select clause with dynamic fields
//        cq.multiselect(selections);
//        List<Object[]> resultList = entityManager.createQuery(cq).getResultList();
//        ObjectMapper mapper = new ObjectMapper();
//        resultList.forEach(result-> {
//            try {
//                System.out.println(mapper.writeValueAsString(result));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            // print json as string
//        });
        return cq;
    }

    @Override
    public <T> Class<T> createJoinQuery(Class<T> dtoClass, ClauseBuilder predicates) {
        Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates1 = predicates.getPredicates();
        HashMap<PREDICATE_CONJUNCTION, Clause> poll = predicates1.poll();
        poll.containsKey(PREDICATE_CONJUNCTION.AND);
        return null;
    }


}
