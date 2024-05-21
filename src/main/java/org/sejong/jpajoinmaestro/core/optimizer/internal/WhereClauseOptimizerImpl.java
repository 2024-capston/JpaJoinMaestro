package org.sejong.jpajoinmaestro.core.optimizer.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.sejong.jpajoinmaestro.core.optimizer.spi.WhereClauseOptimizer;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WhereClauseOptimizerImpl implements WhereClauseOptimizer {
	@PersistenceContext
	private EntityManager entityManager;

	/*
	조건절을 위한 임시클래스
	 */
	@Getter
	@Setter
	private class Condition<T> {
		public enum OperatorType { //조건절 종류
			EQUAL, MORE_THAN, LESS_THAN, MORE_AND_EQUAL, LESS_AND_EQUAL, BETWEEN, IN, LIKE;
			// 유효한 타입인지 검증
			public static boolean isValid(String inputType) {
				for (OperatorType t : OperatorType.values()) {
					if (t.name().equals(inputType)) {
						return true;
					}
				}
				return false;
			}
		}
		private OperatorType operatorType;
		private String columnName; //조건절에 사용되는 컬럼 이름(좌항)
		private T value; //조건절에 사용되는 값. (우항)
		private int indexWeight;
		private int weight=0; // 조건절 순서 재배치 시 사용될 가중치?
		public Condition (OperatorType operatorType, String columnName, T value, int indexWeight) {
			//if (OperatorType.isValid(operatorType)) throw new IllegalArgumentException("Invalid operator type : " + columnType);
			this.operatorType = operatorType;
			this.columnName = columnName;
			this.value = value; //TODO?? value와 operatorType이 성립가능한지도 검사해야 할 듯
			this.indexWeight = indexWeight;
		}
	}
	@Override
	public <T> CriteriaQuery<T> getOptimizedWhereClause(Class<?> dtoClass, Class<?> clause) {
		/**
		 * Entity + 컬럼 + 조인 조건
		 *
		 * TODO?? : 조건절에서 인덱스 컬럼에 별도의 연산을 넣지 않도록 하기? 근데 해줄 수 있는게 있는지는 모르곘음
		 */
		List<Condition> conditions = new ArrayList<>();
		conditions.add(new Condition<>(Condition.OperatorType.MORE_THAN, "price", 100000L, 1));
		replaceColumns(conditions);
		if (isIndexSkipScanNeeded(conditions)) /*index_ss 힌트 추가? */;
		return null; // TODO : return CQ;
	}

	private void replaceColumns(List<Condition> conditions) {
		/**
		 * operator 종류와 index 여부에 따라 조건절 재배치 (가중치만 매기기? or List를 정렬?)
		 * 등치조건을 부등호 조건보다 앞에 오도록 하는 등
		 * 인덱스 선행컬럼이고 등치조건이면 앞쪽으로
		 * 인덱스 선행컬럼인데 부등호면 그 다음
		 * 인덱스 후행컬럼이고 등치조건이면 그 다음
		 * 인덱스 후행컬럼이고 부등호면 그 다음
		 * 인덱스 없으면 상관없겠지?
		 *
		 * for (condition : conditions)
		 * 	- condition.column이 인덱스면
		 * 		- condition.setWeight((인덱스의 컬럼들 개수 - 인덱스 중 컬럼이 몇 번째?) * 10 + (조건 등치임?1:0))
		 * 	- condition.column이 인덱스 아니면
		 * 		- condition.setWeight(0)
		 * 	TODO : 카디널리티 또한 고려하여 가중치 매기기
		 */
		for (Condition condition : conditions) {
			int weight = 0;
			if (condition.getIndexWeight() > 0) {
				// 인덱스 컬럼일 경우
				weight = condition.getIndexWeight() * 10;
				if (condition.getOperatorType() == Condition.OperatorType.EQUAL) {
					weight += 5; // EQUAL 조건일 경우 추가 가중치
				}
			}
			condition.setWeight(weight);
		}

		// 가중치를 기준으로 내림차순 정렬
		conditions.sort((c1, c2) -> Integer.compare(c2.getWeight(), c1.getWeight()));

		return;
	}
	private void likeToBetween(List<Condition> conditions) {
		/**
		 * Like --> Between. 바꿀 수 있으면 바꾸기
		 * - 접두사 매칭 (LIKE "SEOUL%"같은)
		 *
		 */
		for (int i = 0; i < conditions.size(); i++) {
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
		}
	}
	private boolean isIndexSkipScanNeeded(List<Condition> conditions) {
		/**
		 * 인덱스 선행컬럼에 대한 Between 절이 포함되어있다면, Index Skip Scan을 유도하게 하기
		 * 힌트를 넣어야 하는데 어떤 식으로 넣게될지 모르니 일단 넣기 or 넣지 않기로 리턴
		 */
		for(Condition condition : conditions) {
			if (condition.getIndexWeight() > 0) {
				//TODO : 인덱스 컬럼이면 진입하도록 했는데, 선행컬럼인지 따져서, (선행컬럼없고 후행컬럼을 범위검색) or (선행컬럼이 범위검색) 인 경우를 따져야 함
				if (condition.getOperatorType() == Condition.OperatorType.BETWEEN) return true;
			}
		}

		return false;
	}
}
