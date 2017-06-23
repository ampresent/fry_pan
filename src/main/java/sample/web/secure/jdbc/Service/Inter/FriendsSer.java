package sample.web.secure.jdbc.Service.Inter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sample.web.secure.jdbc.Domain.UserInfo;

import java.util.List;

/**
 * Created by wuyihao on 5/6/17.
 */
public interface FriendsSer {
    void follow(String username, String friend);
    boolean isFriends(String user1, String user2);
    void unfollow(String username, String friend);
    List<UserInfo> famousUser(Pageable pageable);
    String[] getFollowing(String user);
}
