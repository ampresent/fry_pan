package sample.web.secure.jdbc.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;
import javax.transaction.TransactionScoped;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by wuyihao on 5/6/17.
 */
@Transactional
public interface FriendshipRepository extends CrudRepository<Friendship, Long>{
    boolean existsByFollowerAndFollowee(String follower, String followee);
    boolean deleteByFollowerAndFollowee(String follower, String followee);
    @Query("SELECT DISTINCT f.followee, COUNT(*) FROM Friendship f GROUP BY f.followee ORDER BY COUNT(*) DESC")
    Page<Object> famousUser(Pageable pageable);
    @Query("SELECT f.followee FROM Friendship f WHERE follower = ?1")
    String[] selectFolloweeByFollower(String follower);
}
