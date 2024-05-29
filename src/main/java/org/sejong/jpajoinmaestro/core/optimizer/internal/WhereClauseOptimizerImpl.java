package org.sejong.jpajoinmaestro.core.optimizer.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.core.extractor.Extractor.Extractor;
import org.sejong.jpajoinmaestro.core.extractor.domain.ExtractedIndex;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;
import org.sejong.jpajoinmaestro.core.query.clause.*;

import java.lang.reflect.Field;
import java.util.*;

@RequiredArgsConstructor
public class WhereClauseOptimizerImpl implements WhereClauseOptimizer {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Clause>> getOptimizedWhereClause(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates) {
		/**
		 * Entity + 컬럼 + 조인 조건
		 *
		 * TODO?? : 조건절에서 인덱스 컬럼에 별도의 연산을 넣지 않도록 하기? 근데 해줄 수 있는게 있는지는 모르겠음.
		 */;
		//Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates = pb.getClauses();

		//먼저 predciates에 대한 가장 가능성 높은 인덱스들 추출
		HashMap<Class<?>,ExtractedIndex> mostLikelyIndexes = getMostLikelyIndexes(dtoClass, predicates);

		likeToBetween(predicates);
		if (isIndexSkipScanNeeded(predicates, mostLikelyIndexes)) System.out.println("index_skip_scan 힌트 ㄱㄱ");
		return replaceClauses(predicates, mostLikelyIndexes);
	}

	/**
	 *
	 * @param predicates : 쿼리 조건절 predicates
	 * @param mostLikelyIndexes : 쿼리에 대한 가장 가능성 높은 인덱스
	 *                   predicates의 각 predicate 객체들을 효율적으로 재배치
	 */
	private PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Clause>> replaceClauses(Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates, HashMap<Class<?>,ExtractedIndex> mostLikelyIndexes) {
		for (HashMap<PREDICATE_CONJUNCTION, Clause> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : map.entrySet()) {
				PREDICATE_CONJUNCTION key = entry.getKey();
				Clause value = entry.getValue();

				ExtractedIndex index = mostLikelyIndexes.get(value.getDomainClass());
				//현재 Clause의 도메인 클래스에 대한 mostLikelyIndex 추출

				double weight = 0;
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

		/* TODO : 처음부터 Clauses를 Priority Queue로 구현? (지금은 새 큐를 만들어 반환) + CONJUNCTION도 바꿔야 함??*/
		PriorityQueue<HashMap<PREDICATE_CONJUNCTION, Clause>> sortedClauses = new PriorityQueue<>((p1, p2)->{
			for(Map.Entry<PREDICATE_CONJUNCTION, Clause> entry1 : p1.entrySet()) {
				for(Map.Entry<PREDICATE_CONJUNCTION, Clause> entry2 : p2.entrySet()) {
					return (int)(entry2.getValue().getWeight()-entry1.getValue().getWeight());
				}
			}
			return 0;
		});
		for(HashMap<PREDICATE_CONJUNCTION, Clause> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : map.entrySet()) {
				HashMap<PREDICATE_CONJUNCTION, Clause> current = new HashMap<>();
				current.put(entry.getKey(), entry.getValue());
				sortedClauses.add(current);
			}
		}

		return sortedClauses;
	}

	/**
	 *
	 * @param predicates : predicate 객체들 중 LIKE "어쩌고%" 이면 between으로 바꾼다.
	 *                   TODO : 숫자형일 때도 바꿔주는게 좋을지도?
	 */
	private void likeToBetween(Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates) {
		for (HashMap<PREDICATE_CONJUNCTION, Clause> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : map.entrySet()) {
				Clause predicate = entry.getValue();
				if (!predicate.getFlag().equals(CONDITION_FLAG.LIKE)) continue;

				Object likeValue = ((Like)predicate).getValue();
				if (predicate.getFlag() == CONDITION_FLAG.LIKE && likeValue instanceof String) {
					String value = (String) likeValue; // LIKE 연산 + value가 String

					if (!value.startsWith("%") && value.endsWith("%")) { // 접두사 매칭일 때
						String prefix = value.substring(0, value.length() - 1);
						String startValue = prefix;
						String endValue = prefix + Character.MAX_VALUE;

						// BETWEEN 조건으로 변경
						Clause betweenClause = new Between().between(predicate.getDomainClass(), predicate.getFieldName(), startValue, endValue);
						entry.setValue(betweenClause);
					}
				}
			}
		}
		// between으로 성공적으로 바뀌는 것은 확인함. TODO : 실제 쿼리 결과도 똑같은지 확인해야.
	}

	/**
	 * 인덱스 선행 컬럼에 대한 Between 절이 포함되어 있다면, Index Skip Scan을 유도하기
	 * TODO : 인덱스 선두 컬럼에 대한 조건이 없고 && 다른 인덱스 컬럼은 범위검색하고 && 선두 컬럼이 enum일 때
	 * @param predicates : 쿼리 조건절 predicates
	 * @param mostLikelyIndexes : 쿼리 수행 시 가장 사용가능성 높은 인덱스
	 * @return : Index Skip Scan 여부
	 */
	private boolean isIndexSkipScanNeeded(Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates, HashMap<Class<?>,ExtractedIndex> mostLikelyIndexes) {
		for (HashMap<PREDICATE_CONJUNCTION, Clause> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : map.entrySet()) {
				Clause predicate = entry.getValue();

				if (mostLikelyIndexes.get(predicate.getDomainClass()).getIndexWeightOfColumn(predicate.getFieldName()) > 0.5) {
					if (predicate.getFlag() == CONDITION_FLAG.BETWEEN || predicate.getFlag() == CONDITION_FLAG.LIKE) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * @param dtoClass : 쿼리 결과로 받고싶은 dtoClass
	 * @param predicates : 쿼리 조건절
	 * @return : <domainClass, 가장 사용 가능성이 높은 INDEX> 쌍
	 */
	private HashMap<Class<?>, ExtractedIndex> getMostLikelyIndexes(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Clause>> predicates) {
        Extractor extractor = new Extractor(entityManager);
		List<Class<?>> domainClasses = new ArrayList<>(); //dtoClass에서 참조하는 domainClass들
		for(Field f : dtoClass.getDeclaredFields()) { //dtoClass의 각 필드에 대해,
			DTOFieldMapping dtoFieldMapping = f.getAnnotation(DTOFieldMapping.class);
			if(dtoFieldMapping != null) {
				Class<?> domainClass = dtoFieldMapping.domain(); //그 필드의 domainClass가
				if (!domainClasses.contains(domainClass)) domainClasses.add(domainClass); //아직 List에 없으면 넣기
			}
		}

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
				for (HashMap<PREDICATE_CONJUNCTION, Clause> predicate : predicates) {
					for (Map.Entry<PREDICATE_CONJUNCTION, Clause> entry : predicate.entrySet()) {
						Clause value = entry.getValue();
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
				System.out.println(entityIndex);
				for (Class<?> otherDomainClass : domainClasses) {
					if (otherDomainClass.equals(domainClass)) continue;
					for(Field f : otherDomainClass.getDeclaredFields()) {
						JoinColumn joinColumn = f.getAnnotation(JoinColumn.class);
						if (joinColumn != null) System.out.println(joinColumn.name());
						//근데 지금 extractedIndex에 FK들은 안들어오는 듯??? (ex : shipment의 orders_id 컬럼)
					}
				}
				System.out.println("---");

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
