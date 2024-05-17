package org.sejong.jpajoinmaestro.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.lang.reflect.Field;

public class CustomRepositoryImpl<T,ID> implements CustomRepository<T,ID> {

    @PersistenceContext
    private EntityManager em;

    @Override
    public T customMethod(Class<T> domainClass, Long id) {
        // TODO: join 비즈니스 로직 추가
        JpaEntityInformation<T, ?> entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, em);
        Class<T> javaType = entityInformation.getJavaType();
        Field[] declaredFields = javaType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField.getName());
        }
        return em.find(domainClass, id);
    }

    @Override
    public T customMethod(Class<T> domainClass, Class<?> myDto, Long id) {
        System.out.println(myDto.getName());
        Field[] declaredFields = myDto.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField.getName());
        }
        return null;
    }

    @Override
    public Class<T> getOurClass() {
        return null;
    }
}