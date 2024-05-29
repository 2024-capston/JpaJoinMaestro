package org.sejong.jpajoinmaestro.core.extractor.domain;

public class ExtractedIndex {
    /**
     * if the index is primary key, then it is null
     */
    private String name;
    private Boolean unique;
    /**
     * example: "username, email"
     */
    private String columnList;
    private Boolean isPrimaryKey;

    public ExtractedIndex(String name, Boolean unique, String columnList, Boolean isPrimaryKey) {
        this.name = name;
        this.unique = unique;
        this.columnList = columnList;
        this.isPrimaryKey = isPrimaryKey;
    }

    public String getName() {
        return name;
    }

    public Boolean getUnique() {
        return unique;
    }

    public String getColumnList() {
        return columnList;
    }

    public Boolean getIsPrimaryKey() {
        return isPrimaryKey;
    }

    public double getIndexWeightOfColumn(String column) {
        /**
         * 추가 : 해당 컬럼이 인덱스에 포함이면
         * (전체 사이즈 - 컬럼의 인덱스 순서)/(전체 사이즈로) 선행컬럼이 더 높은 값을 가지게 하며,
         * 미포함이면 0
         * finally, 가중치가 0.5보다 크면 선행컬럼이라고 볼 수 있겠음
         */
        return columnList.contains(column) ? (double) (columnList.length() - columnList.indexOf(column)) / columnList.length() : 0;
    }

    @Override
    public String toString() {
        return "ExtractedIndex{" +
                "name='" + name + '\'' +
                ", unique=" + unique +
                ", columnList='" + columnList + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                '}';
    }
}
