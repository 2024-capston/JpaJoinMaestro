package org.sejong.jpajoinmaestro.core.extractor.domain;

public class ExtractedForeignKey {
    private final String name;
    private final String referencedColumnName;

    public ExtractedForeignKey(String name, String referencedColumnName) {
        this.name = name;
        this.referencedColumnName = referencedColumnName;
    }

    public String getName() {
        return this.name;
    }

    public String getReferencedColumnName() {
        return this.referencedColumnName;
    }
}

