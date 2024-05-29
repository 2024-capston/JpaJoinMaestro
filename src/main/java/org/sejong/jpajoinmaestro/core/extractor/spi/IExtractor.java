package org.sejong.jpajoinmaestro.core.extractor.spi;

import org.sejong.jpajoinmaestro.core.extractor.domain.ExtractedForeignKey;
import org.sejong.jpajoinmaestro.core.extractor.domain.ExtractedIndex;

import java.util.List;

public interface IExtractor {
    List<ExtractedIndex> getEntityIndexes(Class<?> domainClass);
    List<ExtractedForeignKey> getEntityForeignKeys(Class<?> domainClass);
}
