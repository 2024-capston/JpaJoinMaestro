package org.sejong.jpajoinmaestro.config;

import org.sejong.jpajoinmaestro.repository.CustomRepository;
import org.sejong.jpajoinmaestro.repository.CustomRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

@Configuration
public class CustomConfig {
    @Bean
    public <T,ID> CustomRepository<T,ID> customRepository() {
        return new CustomRepositoryImpl<>();
    }

}
