package org.sejong.jpajoinmaestro.test.config;

import jakarta.persistence.EntityManager;
import lombok.Setter;
import org.sejong.jpajoinmaestro.test.spi.JoinMaestroRepository;
import org.sejong.jpajoinmaestro.test.spi.JoinMaestroRepositoryImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import java.io.Serializable;

public class JoinMaestroRepositoryFactoryBean<R extends JoinMaestroRepository<T, ID>, T, ID extends Serializable>
        implements FactoryBean<R> {

    private EntityManager entityManager;
    private Class<R> repositoryInterface;

    public JoinMaestroRepositoryFactoryBean(Class<R> repositoryInterface) {
        this.repositoryInterface = repositoryInterface;
    }

    @Override
    public R getObject() {
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation((Class<T>) repositoryInterface, entityManager);
        return (R) new JoinMaestroRepositoryImpl<>(entityInformation, entityManager);
    }

    @Override
    public Class<?> getObjectType() {
        return repositoryInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
