package org.sejong.jpajoinmaestro.test.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.sejong.jpajoinmaestro.test.spi.JoinMaestroRepository;
import org.sejong.jpajoinmaestro.test.spi.JoinMaestroRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

import java.io.Serializable;

@Configuration
public class JoinMaestroRepositoryFactory {
    @PersistenceContext
    private EntityManager entityManager;

    // Method to create a repository for any entity type
    public <T, ID extends Serializable> JoinMaestroRepository<T, ID> getRepository(Class<T> domainClass) {
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager);
        return new JoinMaestroRepositoryImpl<>(entityInformation, entityManager);
    }
}