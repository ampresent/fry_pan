package sample.web.secure.jdbc.Service.Inter;

import org.springframework.security.core.userdetails.UserDetailsService;
import sample.web.secure.jdbc.Domain.UserInfo;

/**
 * Created by wuyihao on 5/13/17.
 */
public interface UserSer {
    void register(UserInfo credentials);
    UserInfo find(String username);
    void secZoneInit(String username, String plain, String enc) throws Exception;
}
