package sample.web.secure.jdbc.Domain;

import java.io.Serializable;
import java.util.*;

import org.hibernate.annotations.Type;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class UserInfo {
    @Id
    @NotNull
    @Column(length = 32)
    private String username;
    @NotNull
    @Column(length = 32)
    private String password;
    @NotNull
    private String email;
    @NotNull
    @Type(type="yes_no")
    private boolean enabled;

    @Column(length = 512)
    private String plain;
    @Column(length = 512)
    private String enc;

    public String getPlain() {
        return plain;
    }

    public void setPlain(String plain) {
        this.plain = plain;
    }

    public String getEnc() {
        return enc;
    }

    public void setEnc(String enc) {
        this.enc = enc;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserInfo(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = true;
    }

    public UserInfo() {
        this.enabled = true;
    }

}
