package sample.web.secure.jdbc.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by wuyihao on 5/6/17.
 */
@Transactional
public interface ShareRepository extends CrudRepository<Share, Long> {
    Page<Share> findByFileMeta_Username(String username, Pageable pageable);
    void deleteByFileMeta_UsernameAndIdIn(String username, long[] id);
    Page<Share> findByFileMeta_UsernameInOrderByLikesDesc(String[] usernames, Pageable pageable);
}
