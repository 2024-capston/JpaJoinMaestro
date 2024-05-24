package org.sejong.jpajoinmaestro.core.optimizer.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.annotations.DTOFieldMapping;
import org.sejong.jpajoinmaestro.core.extractor.Extractor.Extractor;
import org.sejong.jpajoinmaestro.core.extractor.domain.ExtractedIndex;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;
import org.sejong.jpajoinmaestro.core.query.clause.Equal;
import org.sejong.jpajoinmaestro.core.query.clause.More;
import org.sejong.jpajoinmaestro.core.query.clause.PredicateBuilder;
import org.sejong.jpajoinmaestro.core.query.constants.CONDITION_FLAG;
import org.sejong.jpajoinmaestro.core.query.constants.PREDICATE_CONJUNCTION;
import org.sejong.jpajoinmaestro.domain.OrderDetail;
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
		PredicateBuilder pb = new PredicateBuilder(new More().than(Shipment.class, "shipmentStatus", "TRANSIT")).and(new Equal().to(Orders.class, "userId", 1));
		Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates = pb.getPredicates();

		replaceColumns(dtoClass, predicates);
		//if (isIndexSkipScanNeeded(predicates)) /*index_ss 힌트 추가? */;
		return null; // TODO : return CQ;
	}

	private void replaceColumns(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
		/**
		 * operator 종류와 index 여부에 따라 조건절 재배치 (가중치만 매기기? or List를 정렬?)
		 * 등치조건을 부등호 조건보다 앞에 오도록 하는 등
		 * 인덱스 선행컬럼이고 등치조건이면 앞쪽으로
		 * 인덱스 선행컬럼인데 부등호면 그 다음
		 * 인덱스 후행컬럼이고 등치조건이면 그 다음
		 * 인덱스 후행컬럼이고 부등호면 그 다음
		 * 인덱스 없으면 상관없겠지?
		 *
		 * 	TODO : 카디널리티 또한 고려하여 가중치 매기기?
		 */
		//먼저 predciates에 대한 가장 가능성 높은 인덱스 추출
		List<HashMap<Class<?>,ExtractedIndex>> mostPossibleIndexes = getMostPossibleIndex(dtoClass, predicates);
		for(HashMap<Class<?>,ExtractedIndex> indexes : mostPossibleIndexes) {
			for(Map.Entry<Class<?>,ExtractedIndex> entry : indexes.entrySet()) {
				System.out.println(entry.getKey().toString() + "::" + entry.getValue().toString());
			}
		}
		for (HashMap<PREDICATE_CONJUNCTION, Predicate> map : predicates) {
			for (Map.Entry<PREDICATE_CONJUNCTION, Predicate> entry : map.entrySet()) {
				PREDICATE_CONJUNCTION key = entry.getKey();
				Predicate value = entry.getValue();

			}
		}

		/*for (Condition condition : conditions) {
			int weight = 0;
			if (condition.getIndexWeight() > 0) {
				// 인덱스 컬럼일 경우
				weight = condition.getIndexWeight() * 10;
				if (condition.getOperatorType() == Condition.OperatorType.EQUAL) {
					weight += 5; // EQUAL 조건일 경우 추가 가중치
				}
			}
			condition.setWeight(weight);
		}*/

		// 가중치를 기준으로 내림차순 정렬
		//conditions.sort((c1, c2) -> Integer.compare(c2.getWeight(), c1.getWeight()));

		return;
	}
	private void likeToBetween(Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
		/**
		 * Like --> Between. 바꿀 수 있으면 바꾸기
		 * - 접두사 매칭 (LIKE "SEOUL%"같은)
		 *
		 */
		/*for (int i = 0; i < conditions.size(); i++) {
			Condition<?> condition = conditions.get(i);

			if (condition.getOperatorType() == Condition.OperatorType.LIKE && condition.getValue() instanceof String) {
				String value = (String) condition.getValue(); //LIKE연산 + value가 String

				if (!value.startsWith("%") && value.endsWith("%")) { //접두사 매칭일 때
					String prefix = value.substring(0, value.length() - 1);
					String startValue = prefix;
					String endValue = prefix + Character.MAX_VALUE;

					conditions.set(i, new Condition<>(Condition.OperatorType.BETWEEN, condition.getColumnName(), new String[] {startValue, endValue}, condition.getIndexWeight()));
				}
			}
		}*/
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
	private List<HashMap<Class<?>, ExtractedIndex>> getMostPossibleIndex(Class<?> dtoClass, Queue<HashMap<PREDICATE_CONJUNCTION, Predicate>> predicates) {
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
		List<HashMap<Class<?>, ExtractedIndex>> mostPossibleEntityIndexes = new ArrayList<>();
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
				System.out.println(domainClass + "=>" + entityIndex.toString() + "=>" + "index possibility : " + score);
				if (score > max_score) { //점수가 높으면 bestIndex 갱신
					max_score = score;
					bestIndex = entityIndex;
				}
			}
			HashMap<Class<?>, ExtractedIndex> domainAndIndex = new HashMap<>();
			domainAndIndex.put(domainClass, bestIndex); //bestIndex로 <domainClass-ExtractedIndex>쌍 생성
			mostPossibleEntityIndexes.add(domainAndIndex); //리스트에 추가
		}
		System.out.println("done.");
		return mostPossibleEntityIndexes;
	}
}
