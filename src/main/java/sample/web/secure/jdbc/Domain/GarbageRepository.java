package sample.web.secure.jdbc.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wuyihao on 5/25/17.
 */
@Transactional
public interface GarbageRepository extends CrudRepository<Garbage, Long> {
    Page<Garbage> findByFileMeta_username(String username, Pageable p);
    Iterable<Garbage> findByFileMeta_username(String username);
}
