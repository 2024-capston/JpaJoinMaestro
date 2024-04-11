package org.sejong.jpajoinmaestro.test.spi;
import org.springframework.data.repository.NoRepositoryBean;
/**
 * 추상화부분
 * @param <T>
 * @param <ID>
 */
@NoRepositoryBean
public interface JoinMaestroRepository<T, ID> {
    T findById(ID id);
}