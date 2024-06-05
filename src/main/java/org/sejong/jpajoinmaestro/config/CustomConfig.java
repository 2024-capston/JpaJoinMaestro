package org.sejong.jpajoinmaestro.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.sejong.jpajoinmaestro.repository.CustomRepository;
import org.sejong.jpajoinmaestro.repository.CustomRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

@Configuration
public class CustomConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public <T,ID> CustomRepository<T,ID> customRepository() {
        return new CustomRepositoryImpl<>();
    }
}
