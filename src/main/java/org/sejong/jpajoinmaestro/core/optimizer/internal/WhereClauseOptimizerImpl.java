package org.sejong.jpajoinmaestro.core.optimizer.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.core.extractor.Extractor.Extractor;
import org.sejong.jpajoinmaestro.core.extractor.domain.ExtractedIndex;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.core.query.clause.Equal;
import org.sejong.jpajoinmaestro.core.query.clause.More;
import org.sejong.jpajoinmaestro.core.query.clause.PredicateBuilder;
import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;
import org.sejong.jpajoinmaestro.core.query.clause.*;
import org.sejong.jpajoinmaestro.domain.Orders;
import org.sejong.jpajoinmaestro.domain.Shipment;

import java.lang.reflect.Field;
import java.util.*;

@RequiredArgsConstructor
public class WhereClauseOptimizerImpl implements WhereClauseOptimizer {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public <T> CriteriaQuery<T> getOptimizedWhereClause(Class<?> dtoClass, Class<?> clause) {
		/**
		 * Entity + 컬럼 + 조인 조건
		 *
		 * TODO?? : 조건절에서 인덱스 컬럼에 별도의 연산을 넣지 않도록 하기? 근데 해줄 수 있는게 있는지는 모르곘음
		 */
		PredicateBuilder pb = new PredicateBuilder(new More().than(Shipment.class, "shipmentStatus", "TRANSIT")).and(new Equal().to(Orders.class, "userId", 1)).and(new Like().with(Orders.class, "status", "DONE%"));
		Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates = pb.getPredicates();

		likeToBetween(predicates);
		replacePredicates(dtoClass, predicates);
		//if (isIndexSkipScanNeeded(predicates)) /*index_ss 힌트 추가? */;
		return null;
	}

	/**
	 *
	 * @param dtoClass : 쿼리 결과로 받을 dtoClass
	 * @param predicates : 쿼리 조건절 predicates
	 *                   predicates의 각 predicate 객체들을 효율적으로 재배치
	 */
	private void replacePredicates(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
		//먼저 predciates에 대한 가장 가능성 높은 인덱스들 추출
		HashMap<Class<?>,ExtractedIndex> mostLikelyIndexes = getMostLikelyIndexes(dtoClass, predicates);

		for (HashMap<PREDICATE_CONJUNCTION, Predicate> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Predicate> entry : map.entrySet()) {
				PREDICATE_CONJUNCTION key = entry.getKey();
				Predicate value = entry.getValue();

				ExtractedIndex index = mostLikelyIndexes.get(value.getDomainClass());
				//현재 Predicate의 도메인 클래스에 대한 mostLikelyIndex 추출

				double weight = 0;
				System.out.println(index.toString() + "/" + value.getDomainClass() + "/" + value.getFieldName());
				if (value.getFlag().equals(CONDITION_FLAG.EQUAL)) {
					weight += 10 * index.getIndexWeightOfColumn(value.getFieldName());
				} //등치조건이면 10*인덱스가중치, 아니면 5*인덱스가중치
				else {
					weight += 5 * index.getIndexWeightOfColumn(value.getFieldName());
				}
				value.setWeight(weight);
			}
		}
		/*
		* TODO : 잘 재배치 되는지 검증 필요.
		* 필드 명과 실제 컬럼 이름 간의 차이때문에 0으로 getIndexWeightOfColumn이 잘 동작하지 않을 수도?
		* 또는 인덱스가 제대로 찾아졌는지도?
		* */

		/* TODO : PriorityQueue로 Predicates를 담거나, 아예 ArrayList로 담아서 정렬하던지, ..  + CONJUNCTION도 바꿔야 함??*/
		PriorityQueue<Predicate> sortedPredicates = new PriorityQueue<>((p1, p2)->(int)(p1.getWeight()-p2.getWeight()));
		for(HashMap<PREDICATE_CONJUNCTION, Predicate> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Predicate> entry : map.entrySet()) {
				sortedPredicates.add(entry.getValue());
			}
		}
		for (Predicate predicate : sortedPredicates) {
			System.out.println( predicate.getDomainClass() + ":" + predicate.getFieldName() + ":" + predicate.getFlag() + "=>" + predicate.getWeight());
		}

		return;
	}

	/**
	 *
	 * @param predicates : predicate 객체들 중 LIKE "어쩌고%" 이면 between으로 바꾼다.
	 *                   TODO : 숫자형일 때도 바꿔주는게 좋을지도?
	 */
	private void likeToBetween(Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
		for (HashMap<PREDICATE_CONJUNCTION, Predicate> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Predicate> entry : map.entrySet()) {
				Predicate predicate = entry.getValue();
				if (!predicate.getFlag().equals(CONDITION_FLAG.LIKE)) continue;

				Object likeValue = ((Like)predicate).getValue();
				if (predicate.getFlag() == CONDITION_FLAG.LIKE && likeValue instanceof String) {
					String value = (String) likeValue; // LIKE 연산 + value가 String

					if (!value.startsWith("%") && value.endsWith("%")) { // 접두사 매칭일 때
						String prefix = value.substring(0, value.length() - 1);
						String startValue = prefix;
						String endValue = prefix + Character.MAX_VALUE;

						// BETWEEN 조건으로 변경
						Predicate betweenPredicate = new Between().between(predicate.getDomainClass(), predicate.getFieldName(), startValue, endValue);
						entry.setValue(betweenPredicate);
					}
				}
			}
		}
		// between으로 성공적으로 바뀌는 것은 확인함. TODO : 실제 쿼리 결과도 똑같은지 확인해야.
	}
	private boolean isIndexSkipScanNeeded(Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
		/**
		 * 인덱스 선행컬럼에 대한 Between 절이 포함되어있다면, Index Skip Scan을 유도하게 하기
		 * 힌트를 넣어야 하는데 어떤 식으로 넣게될지 모르니 일단 넣기 or 넣지 않기로 리턴
		 */
		/*for(Condition condition : conditions) {
			if (condition.getIndexWeight() > 0) {
				//TODO : 인덱스 컬럼이면 진입하도록 했는데, 선행컬럼인지 따져서, (선행컬럼없고 후행컬럼을 범위검색) or (선행컬럼이 범위검색) 인 경우를 따져야 함
				if (condition.getOperatorType() == Condition.OperatorType.BETWEEN) return true;
			}
		}*/

		return false;
	}

	/**
	 * @param dtoClass : 쿼리 결과로 받고싶은 dtoClass
	 * @param predicates : 쿼리 조건절
	 * @return : <domainClass, 가장 사용 가능성이 높은 INDEX> 쌍
	 */
	private HashMap<Class<?>, ExtractedIndex> getMostLikelyIndexes(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
        Extractor extractor = new Extractor(entityManager);
		List<Class<?>> domainClasses = new ArrayList<>(); //dtoClass에서 참조하는 domainClass들
		for(Field f : dtoClass.getDeclaredFields()) { //dtoClass의 각 필드에 대해,
			DTOFieldMapping dtoFieldMapping = f.getAnnotation(DTOFieldMapping.class);
			if(dtoFieldMapping != null) {
				Class<?> domainClass = dtoFieldMapping.domain(); //그 필드의 domainClass가
				if (!domainClasses.contains(domainClass)) domainClasses.add(domainClass); //아직 List에 없으면 넣기
			}
		}
		/*
		검증테스트
		for (Class<?> domainClass : domainClasses) {
			List<ExtractedIndex> indexes = extractor.getEntityIndexes(domainClass);
			for (ExtractedIndex index : indexes) {
				System.out.println(domainClass.toString() + " " + index.toString());
			}
		}*/
		//이제 dtoClass에서 참조하는 domainClass들을 알아내었음
		HashMap<Class<?>, ExtractedIndex> mostPossibleEntityIndexes = new HashMap<>();
		for(Class<?> domainClass : domainClasses) {
			//각 domainClass의 인덱스들을 확인
			double max_score = 0;
			ExtractedIndex bestIndex = null;
			for(ExtractedIndex entityIndex : extractor.getEntityIndexes(domainClass)) {
				double score = 0;
				/* PK거나 UniqueIndex이면서 && (조회 컬럼에 포함?)*/
				if ((entityIndex.getIsPrimaryKey() || entityIndex.getUnique()))
					score+=5; //5 or 10

				/* WHERE절에서 사용되는 컬럼임? + 얼마나 선행컬럼임?? */
				for (HashMap<PREDICATE_CONJUNCTION, Predicate> predicate : predicates) {
					for (Map.Entry<PREDICATE_CONJUNCTION, Predicate> entry : predicate.entrySet()) {
						Predicate value = entry.getValue();
						PREDICATE_CONJUNCTION key = entry.getKey();
						score += entityIndex.getIndexWeightOfColumn(value.getFieldName()) * 10;
						/* LIKE "%어쩌구"같이 %가 앞에 오면 index타기 힘드니까 가중치 빼기 */
						if (value.getFlag().equals(CONDITION_FLAG.LIKE) && ((Like) value).getValue().toString().startsWith("%"))
							score -= 4;
					}
				}

				/* ORDER BY절에서 사용되는 컬럼임? + 얼마나 선행컬럼? */
				/* TODO: ORDER BY 아직 없음 */

				/* GROUP BY절에서 사용되는 컬럼임? + 얼마나 선행 컬럼? */
				/* TODO: GROUP BY 아직 없음 */

				/* JOIN에 사용되나? */
				/* TODO : JOIN의 ON절 참조 */
				if (score > max_score) { //점수가 높으면 bestIndex 갱신
					max_score = score;
					bestIndex = entityIndex;
				}
			}
			mostPossibleEntityIndexes.put(domainClass, bestIndex); //리스트에 추가
		}
		return mostPossibleEntityIndexes;
	}
}
