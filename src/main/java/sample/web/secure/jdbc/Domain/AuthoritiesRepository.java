package sample.web.secure.jdbc.Domain;

import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by wuyihao on 5/16/17.
 */
@Transactional
public interface AuthoritiesRepository extends CrudRepository<Authorities, Long> {

}
