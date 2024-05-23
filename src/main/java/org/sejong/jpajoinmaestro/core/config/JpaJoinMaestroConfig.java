package org.sejong.jpajoinmaestro.core.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.sejong.jpajoinmaestro.core.annotations.internal.DTOFieldMappingUtilImpl;
import org.sejong.jpajoinmaestro.core.extractor.Extractor.Extractor;
import org.sejong.jpajoinmaestro.core.extractor.spi.IExtractor;
import org.sejong.jpajoinmaestro.core.query.internal.JoinSelectQueryImpl;
import org.sejong.jpajoinmaestro.core.query.spi.JoinQueryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaJoinMaestroConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public IExtractor extractor() {
        return new Extractor(em);
    }

    @Bean
    public JoinQueryBuilder joinQueryBuilder() {
        return new JoinSelectQueryImpl(em, new DTOFieldMappingUtilImpl());
    }
}
