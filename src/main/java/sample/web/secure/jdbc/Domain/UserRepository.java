package sample.web.secure.jdbc.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.userdetails.User;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by wuyihao on 5/13/17.
 */
@Transactional
public interface UserRepository extends CrudRepository<UserInfo, String>{
    UserInfo findByEmail(String email);
    UserInfo findByUsername(String username);
    @Query("SELECT u FROM UserInfo u ORDER BY RAND()")
    Page<UserInfo> getRandom(Pageable p);
    @Query("SELECT u FROM UserInfo u WHERE u.username NOT IN ?1 ORDER BY RAND()")
    Page<UserInfo> getRandomExcept(List<String> username, Pageable p);
    List<UserInfo> findByUsernameIn(List<String> usernames);
}
