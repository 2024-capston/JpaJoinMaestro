package org.sejong.jpajoinmaestro.core.query.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.core.annotations.spi.DTOFieldMappingUtil;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.core.query.clause.*;
import org.sejong.jpajoinmaestro.core.query.constants.FIELD_TYPE;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;
import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
import org.sejong.jpajoinmaestro.dto.ShipmentOrder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

public class JoinSelectQueryImpl implements JoinQueryBuilder {
    private static final Logger LOGGER = Logger.getLogger(JoinSelectQueryImpl.class.getName());
    private static final JoinType DEFAULT_JOIN_TYPE = JoinType.INNER;

    private final EntityManager entityManager;
    private final DTOFieldMappingUtil dtoFieldMapping;
    private final WhereClauseOptimizer whereClauseOptimizer;

    public JoinSelectQueryImpl(EntityManager entityManager, DTOFieldMappingUtil dtoFieldMapping, WhereClauseOptimizer whereClauseOptimizer) {
        this.entityManager = entityManager;
        this.dtoFieldMapping = dtoFieldMapping;
        this.whereClauseOptimizer = whereClauseOptimizer;
    }

    @Override
    public <T> List<T> createJoinQuery(Class<T> dtoClass, ClauseBuilder predicates) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Set<Class<?>> entities = new LinkedHashSet<>();

        List<Field> selectFieldList = new ArrayList<>(Arrays.asList(dtoClass.getDeclaredFields()));
        List<Selection<?>> selections = new ArrayList<>();

        Map<Predicate, PREDICATE_CONJUNCTION> predicateMap = new LinkedHashMap<>();
        Map<Predicate, Double> predicateWeightMap = new LinkedHashMap<>();
        Map<Predicate, Integer> predicateGroupIdMap = new LinkedHashMap<>();

        Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> remainClauseList = whereClauseOptimizer.getOptimizedWhereClause(ShipmentOrder.class, predicates.getPredicates());
        List<Field> resultFieldList = new ArrayList<>();

        Root<?> root = null;
        Join<?, ?> joinRoot = null;

        // Step 1: Find all entities
        for (Field field : selectFieldList) {
            Class<?> domainClass = dtoFieldMapping.getDomainClass(field);
            entities.add(domainClass);
        }

        // Step 2: Topological Sort Entities
        List<Class<?>> sortedEntities = topologicalSort(entities);

        // Step 3: Set root & root select field & add predicate
        Class<?> rootEntity = sortedEntities.get(0);
        root = cq.from(rootEntity);
        setSelectFieldFromDomain(root, selectFieldList, rootEntity, selections, resultFieldList);
        addPredicate(cb, root, remainClauseList, rootEntity, predicateMap, predicateWeightMap, predicateGroupIdMap);

        // Step 4: Update joinRoot & joinRoot select field  & add predicate
        for (Class<?> entity : sortedEntities) {
            joinRoot = processEntityForJoin(cb, cq, entity, entities, joinRoot, root, selectFieldList, selections, resultFieldList, remainClauseList, predicateMap, predicateWeightMap, predicateGroupIdMap);
        }

        cq.multiselect(selections);

        Predicate finalPredicate = buildFinalPredicate(cb, predicateMap, predicateWeightMap, predicateGroupIdMap);
        if (finalPredicate != null) {
            cq.where(finalPredicate);
        }

        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query.getResultList();

        logResults(results, resultFieldList);


        return mapResultsToDTO(results, resultFieldList, dtoClass);
    }

    private <T> List<T> mapResultsToDTO(List<Object[]> results, List<Field> resultFieldList, Class<T> dtoClass) {
        List<T> mappedResults = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        for (Object[] result : results) {
            try {
                T dtoInstance = dtoClass.getDeclaredConstructor().newInstance();

                for (int i = 0; i < result.length; i++) {
                    Field field = resultFieldList.get(i);
                    field.setAccessible(true);
                    field.set(dtoInstance, result[i]);
                }

                mappedResults.add(dtoInstance);
            } catch (Exception e) {
                LOGGER.severe("Error mapping results to DTO: " + e.getMessage());
            }
        }

        return mappedResults;
    }

    private Join<?, ?> processEntityForJoin(CriteriaBuilder cb, CriteriaQuery<?> cq, Class<?> entity, Set<Class<?>> entities,
                                            Join<?, ?> joinRoot, Root<?> root, List<Field> selectFieldList,
                                            List<Selection<?>> selections, List<Field> resultFieldList,
                                            Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> remainClauseList,
                                            Map<Predicate, PREDICATE_CONJUNCTION> predicateMap,
                                            Map<Predicate, Double> predicateWeightMap, Map<Predicate, Integer> predicateGroupIdMap) {

        for (Field field : entity.getDeclaredFields()) {
            if (!field.isAnnotationPresent(JoinColumn.class)) {
                continue;
            }

            Class<?> targetEntity = field.getType();
            if (!entities.contains(targetEntity)) {
                continue;
            }

            String attributeName = field.getName();
            joinRoot = (joinRoot == null) ? root.join(attributeName, DEFAULT_JOIN_TYPE) : joinRoot.join(attributeName, DEFAULT_JOIN_TYPE);

            setSelectFieldFromDomain(joinRoot, selectFieldList, targetEntity, selections, resultFieldList);
            addPredicate(cb, joinRoot, remainClauseList, targetEntity, predicateMap, predicateWeightMap, predicateGroupIdMap);

            LOGGER.info("Remaining clause list size: " + remainClauseList.size());
        }
        return joinRoot;
    }

    private Predicate buildFinalPredicate(CriteriaBuilder cb, Map<Predicate, PREDICATE_CONJUNCTION> predicateMap, Map<Predicate, Double> predicateWeightMap, Map<Predicate, Integer> predicateGroupIdMap) {
        // Sort the predicates by groupId ascending, and by weight descending within the same groupId
        LinkedHashMap<Predicate, PREDICATE_CONJUNCTION> sortedMap = new LinkedHashMap<>();

        predicateGroupIdMap.entrySet().stream()
                .sorted((entry1, entry2) -> {
                    int groupComparison = Integer.compare(entry1.getValue(), entry2.getValue());
                    if (groupComparison != 0) {
                        return groupComparison;
                    } else {
                        return Double.compare(predicateWeightMap.get(entry2.getKey()), predicateWeightMap.get(entry1.getKey()));
                    }
                })
                .forEachOrdered(entry -> sortedMap.put(entry.getKey(), predicateMap.get(entry.getKey())));

        // Create the final Predicate
        Predicate finalPredicate = null;
        Predicate currentGroupPredicate = null;
        Integer currentGroupId = null;

        for (Map.Entry<Predicate, PREDICATE_CONJUNCTION> entry : sortedMap.entrySet()) {
            Predicate predicate = entry.getKey();
            Integer groupId = predicateGroupIdMap.get(predicate);

            if (currentGroupId == null || !currentGroupId.equals(groupId)) {
                if (currentGroupPredicate != null) {
                    finalPredicate = (finalPredicate == null) ? currentGroupPredicate : cb.or(finalPredicate, currentGroupPredicate);
                }
                currentGroupPredicate = predicate;
                currentGroupId = groupId;
            } else {
                currentGroupPredicate = (currentGroupPredicate == null) ? predicate : cb.and(currentGroupPredicate, predicate);
            }
        }

        if (currentGroupPredicate != null) {
            finalPredicate = (finalPredicate == null) ? currentGroupPredicate : cb.or(finalPredicate, currentGroupPredicate);
        }

        return finalPredicate;
    }

    private void logResults(List<Object[]> results, List<Field> resultFieldList) {
        ObjectMapper mapper = new ObjectMapper();
        results.forEach(result -> {
            try {
                LOGGER.info(mapper.writeValueAsString(result));
            } catch (JsonProcessingException e) {
                LOGGER.severe("Error processing JSON: " + e.getMessage());
            }
        });

        LOGGER.info(resultFieldList.toString());
    }

    private List<Class<?>> topologicalSort(Set<Class<?>> entities) {
        Map<Class<?>, List<Class<?>>> adjacencyEntity = new HashMap<>();
        Map<Class<?>, Integer> inDegree = new HashMap<>();

        for (Class<?> entity : entities) {
            adjacencyEntity.putIfAbsent(entity, new ArrayList<>());
            inDegree.putIfAbsent(entity, 0);

            for (Field field : entity.getDeclaredFields()) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    Class<?> targetEntity = field.getType();
                    if (entities.contains(targetEntity)) {
                        adjacencyEntity.get(entity).add(targetEntity);
                        inDegree.put(targetEntity, inDegree.getOrDefault(targetEntity, 0) + 1);
                    }
                }
            }
        }

        Queue<Class<?>> queue = new LinkedList<>();
        for (Map.Entry<Class<?>, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<Class<?>> sortedEntities = new ArrayList<>();
        while (!queue.isEmpty()) {
            Class<?> entity = queue.poll();
            sortedEntities.add(entity);

            for (Class<?> neighbor : adjacencyEntity.get(entity)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (sortedEntities.size() != entities.size()) {
            throw new RuntimeException("Cycle detected in the entity graph, topological sort not possible.");
        }

        return sortedEntities;
    }

    private void setSelectFieldFromDomain(From<?, ?> joinRoot, List<Field> selectFieldList, Class<?> targetEntity, List<Selection<?>> selections, List<Field> resultFieldList) {
        Iterator<Field> iterator = selectFieldList.iterator();
        while (iterator.hasNext()) {
            Field field = iterator.next();
            DTOFieldMapping dtoFieldMapping = field.getAnnotation(DTOFieldMapping.class);

            if (dtoFieldMapping.domain().equals(targetEntity)) {
                selections.add(joinRoot.get(dtoFieldMapping.fieldName()));
                iterator.remove();
                resultFieldList.add(field);
            }
        }
    }

    private void addPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> remainClauseList, Class<?> targetEntity, Map<Predicate, PREDICATE_CONJUNCTION> predicateMap, Map<Predicate, Double> predicateWeightMap, Map<Predicate, Integer> predicateGroupIdMap) {
        Iterator<HashMap<PREDICATE_CONJUNCTION, Clause>> iterator = remainClauseList.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            HashMap<PREDICATE_CONJUNCTION, Clause> map = iterator.next();
            boolean isDone = false;

            for (Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : map.entrySet()) {
                PREDICATE_CONJUNCTION conjunction = entry.getKey();
                Clause clause = entry.getValue();

                if (!clause.getDomainClass().equals(targetEntity)) {
                    continue;
                }

                isDone = true;
                addClausePredicate(cb, joinRoot, conjunction, clause, predicateMap, predicateWeightMap, predicateGroupIdMap);
            }

            if (isDone) {
                LOGGER.info("Index of processed item: " + index);
                iterator.remove();
            }
            index++;
        }
    }

    private void addClausePredicate(CriteriaBuilder cb, From<?, ?> joinRoot, PREDICATE_CONJUNCTION conjunction, Clause clause,
                                    Map<Predicate, PREDICATE_CONJUNCTION> predicateMap, Map<Predicate, Double> predicateWeightMap, Map<Predicate, Integer>predicateIntegerMap) {
        Predicate predicate = null;

        String fieldName = clause.getFieldName();

        switch (clause.getFlag()) {
            case MORE_THAN:
                predicate = createGreaterThanPredicate(cb, joinRoot, fieldName, ((More)clause).getValue(), ((More)clause).getValueType());
                break;
            case LESS_THAN:
                predicate = createLessThanPredicate(cb, joinRoot, fieldName, ((Less)clause).getValue(), ((Less)clause).getValueType());
                break;
            case MORE_THAN_EQUAL:
                predicate = createGreaterThanOrEqualPredicate(cb, joinRoot, fieldName, ((More)clause).getValue(), ((More)clause).getValueType());
                break;
            case LESS_THAN_EQUAL:
                predicate = createLessThanOrEqualPredicate(cb, joinRoot, fieldName, ((Less)clause).getValue(), ((Less)clause).getValueType());
                break;
            case EQUAL:
                predicate = createEqualPredicate(cb, joinRoot, fieldName, ((Equal)clause).getValue(), ((Equal)clause).getValueType());
                break;
            case BETWEEN:
                predicate = createBetweenPredicate(cb, joinRoot, fieldName, ((Between)clause).getStart(), ((Between)clause).getEnd(), ((Between)clause).getStartType());
                break;
            case LIKE:
                predicate = createLikePredicate(cb, joinRoot, fieldName, (String)((Like)clause).getValue());
                break;
            default:
                break;
        }

        if (predicate != null) {
            predicateWeightMap.put(predicate, clause.getWeight());
            predicateIntegerMap.put(predicate, clause.getGroupId());
            predicateMap.put(predicate, conjunction);
        }
    }

    private Predicate createGreaterThanPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, Object value, FIELD_TYPE fieldType) {
        switch (fieldType) {
            case STRING:
                return cb.greaterThan(joinRoot.get(fieldName), (String) value);
            case INTEGER:
                return cb.greaterThan(joinRoot.get(fieldName), (Integer) value);
            case LONG:
                return cb.greaterThan(joinRoot.get(fieldName), (Long) value);
            case DOUBLE:
                return cb.greaterThan(joinRoot.get(fieldName), (Double) value);
            case FLOAT:
                return cb.greaterThan(joinRoot.get(fieldName), (Float) value);
            case DATE:
                return cb.greaterThan(joinRoot.get(fieldName), (Date) value);
            case BOOLEAN:
                return cb.greaterThan(joinRoot.get(fieldName), (Boolean) value);
            default:
                return null;
        }
    }

    private Predicate createLessThanPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, Object value, FIELD_TYPE fieldType) {
        switch (fieldType) {
            case STRING:
                return cb.lessThan(joinRoot.get(fieldName), (String) value);
            case INTEGER:
                return cb.lessThan(joinRoot.get(fieldName), (Integer) value);
            case LONG:
                return cb.lessThan(joinRoot.get(fieldName), (Long) value);
            case DOUBLE:
                return cb.lessThan(joinRoot.get(fieldName), (Double) value);
            case FLOAT:
                return cb.lessThan(joinRoot.get(fieldName), (Float) value);
            case DATE:
                return cb.lessThan(joinRoot.get(fieldName), (Date) value);
            case BOOLEAN:
                return cb.lessThan(joinRoot.get(fieldName), (Boolean) value);
            default:
                return null;
        }
    }

    private Predicate createGreaterThanOrEqualPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, Object value, FIELD_TYPE fieldType) {
        switch (fieldType) {
            case STRING:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (String) value);
            case INTEGER:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (Integer) value);
            case LONG:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (Long) value);
            case DOUBLE:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (Double) value);
            case FLOAT:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (Float) value);
            case DATE:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (Date) value);
            case BOOLEAN:
                return cb.greaterThanOrEqualTo(joinRoot.get(fieldName), (Boolean) value);
            default:
                return null;
        }
    }

    private Predicate createLessThanOrEqualPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, Object value, FIELD_TYPE fieldType) {
        switch (fieldType) {
            case STRING:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (String) value);
            case INTEGER:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (Integer) value);
            case LONG:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (Long) value);
            case DOUBLE:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (Double) value);
            case FLOAT:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (Float) value);
            case DATE:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (Date) value);
            case BOOLEAN:
                return cb.lessThanOrEqualTo(joinRoot.get(fieldName), (Boolean) value);
            default:
                return null;
        }
    }

    private Predicate createEqualPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, Object value, FIELD_TYPE fieldType) {
        switch (fieldType) {
            case STRING:
                return cb.equal(joinRoot.get(fieldName), (String) value);
            case INTEGER:
                return cb.equal(joinRoot.get(fieldName), (Integer) value);
            case LONG:
                return cb.equal(joinRoot.get(fieldName), (Long) value);
            case DOUBLE:
                return cb.equal(joinRoot.get(fieldName), (Double) value);
            case FLOAT:
                return cb.equal(joinRoot.get(fieldName), (Float) value);
            case DATE:
                return cb.equal(joinRoot.get(fieldName), (Date) value);
            case BOOLEAN:
                return cb.equal(joinRoot.get(fieldName), (Boolean) value);
            default:
                return null;
        }
    }

    private Predicate createBetweenPredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, Object startValue, Object endValue, FIELD_TYPE fieldType) {
        switch (fieldType) {
            case STRING:
                return cb.between(joinRoot.get(fieldName), (String) startValue, (String) endValue);
            case INTEGER:
                return cb.between(joinRoot.get(fieldName), (Integer) startValue, (Integer) endValue);
            case LONG:
                return cb.between(joinRoot.get(fieldName), (Long) startValue, (Long) endValue);
            case DOUBLE:
                return cb.between(joinRoot.get(fieldName), (Double) startValue, (Double) endValue);
            case FLOAT:
                return cb.between(joinRoot.get(fieldName), (Float) startValue, (Float) endValue);
            case DATE:
                return cb.between(joinRoot.get(fieldName), (Date) startValue, (Date) endValue);
            default:
                return null;
        }
    }

    private Predicate createLikePredicate(CriteriaBuilder cb, From<?, ?> joinRoot, String fieldName, String pattern) {
        return cb.like(joinRoot.get(fieldName), pattern);
    }
}