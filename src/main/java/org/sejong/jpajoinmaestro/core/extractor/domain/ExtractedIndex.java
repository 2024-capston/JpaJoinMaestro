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
