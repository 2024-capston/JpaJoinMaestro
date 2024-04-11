package org.sejong.jpajoinmaestro.test.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "org.sejong.jpajoinmaestro.repository")
public class JoinMaestroConfig {
}
