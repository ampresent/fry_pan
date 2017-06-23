package sample.web.secure.jdbc.Domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wuyihao on 5/6/17.
 */
@Transactional
public interface OfflineTaskRepository extends CrudRepository<OfflineTask, Long>{
    List<OfflineTask> findByUsername(String username);
    void deleteByUsername(String username);
    void deleteByUsernameAndId(String username, Long id);



}
