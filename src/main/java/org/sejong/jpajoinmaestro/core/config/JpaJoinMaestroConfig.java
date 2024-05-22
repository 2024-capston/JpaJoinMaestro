package org.sejong.jpajoinmaestro.core.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.sejong.jpajoinmaestro.core.extractor.Extractor.Extractor;
import org.sejong.jpajoinmaestro.core.extractor.spi.IExtractor;
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
}
