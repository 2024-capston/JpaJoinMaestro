package org.sejong.jpajoinmaestro.test.spi;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.Serializable;

@Repository
public class JoinMaestroRepositoryImpl<T, ID extends Serializable>  implements JoinMaestroRepository<T, ID> {
    private final JpaEntityInformation<T,?> entityInformation;
    private final EntityManager em;

    public JoinMaestroRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.entityInformation = entityInformation;
        this.em = entityManager;
    }

    @Override
    public T findById(ID id) {
        return em.find(entityInformation.getJavaType(), id);
    }
}

