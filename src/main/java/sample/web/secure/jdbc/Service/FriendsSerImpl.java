package sample.web.secure.jdbc.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Domain.Friendship;
import sample.web.secure.jdbc.Domain.FriendshipRepository;
import sample.web.secure.jdbc.Domain.UserInfo;
import sample.web.secure.jdbc.Domain.UserRepository;
import sample.web.secure.jdbc.Service.Inter.FriendsSer;

import java.util.List;

/**
 * Created by wuyihao on 5/6/17.
 */
@Service
public class FriendsSerImpl implements FriendsSer {
    private FriendshipRepository friendshipRepository;
    private UserRepository userRepository;

    @Autowired
    public FriendsSerImpl(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void follow(String username, String friend) {
        friendshipRepository.save(new Friendship(username, friend));
    }

    @Override
    public boolean isFriends(String user1, String user2) {
        return friendshipRepository.existsByFollowerAndFollowee(user1, user2) &&
            friendshipRepository.existsByFollowerAndFollowee(user2, user1);
    }

    @Override
    public void unfollow(String username, String friend) {
        friendshipRepository.deleteByFollowerAndFollowee(username, friend);
    }

    @Override
    public List<UserInfo> famousUser(Pageable pageable) {
        Page<Object> famous = friendshipRepository.famousUser(pageable);
        List<String> famousList = famous.map(new Converter<Object, String>() {
            @Override
            public String convert(Object entity) {
                return (String)((Object[])entity)[0];
            }
        }).getContent();
        List<UserInfo> userList = userRepository.findByUsernameIn(famousList);
        if (famousList.size() < pageable.getPageSize()) {
            Pageable p = new PageRequest(0, pageable.getPageSize() - famousList.size());
            Page<UserInfo> random;
            if (famousList.size() == 0) {
                random = userRepository.getRandom(p);
            } else {
                random = userRepository.getRandomExcept(famousList, p);
            }
            userList.addAll(random.getContent());
        }
        return userList;
    }

    @Override
    public String[] getFollowing(String username) {
        return friendshipRepository.selectFolloweeByFollower(username);
    }
}
