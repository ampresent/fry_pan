package sample.web.secure.jdbc.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Domain.Authorities;
import sample.web.secure.jdbc.Domain.AuthoritiesRepository;
import sample.web.secure.jdbc.Domain.UserInfo;
import sample.web.secure.jdbc.Domain.UserRepository;
import sample.web.secure.jdbc.Service.Inter.UserSer;

import java.util.Arrays;

/**
 * Created by wuyihao on 5/13/17.
 */
@Service
public class UserSerImpl implements UserSer {
    private UserRepository userRepository;
    private AuthoritiesRepository authoritiesRepository;

    @Autowired
    public UserSerImpl(UserRepository userRepository, AuthoritiesRepository authoritiesRepository) {
        this.userRepository = userRepository;
        this.authoritiesRepository = authoritiesRepository;
    }

    public void register(UserInfo credentials) {
        userRepository.save(credentials);
        authoritiesRepository.save(new Authorities(credentials.getUsername(), "ROLE_USER"));

    }

    public UserInfo find(String username) {
        return userRepository.findOne(username);
    }


    public void secZoneInit(String username, String plain, String enc) throws Exception {
        UserInfo userInfo = userRepository.findByUsername(username);
        if (userInfo.getPlain() != null && userInfo.getPlain() != plain ||
                userInfo.getEnc() != null && userInfo.getEnc() != enc)
            throw new Exception("Secure credential already exists");
        userInfo.setPlain(plain);
        userInfo.setEnc(enc);
        userRepository.save(userInfo);
    }

}
