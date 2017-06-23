package sample.web.secure.jdbc.Domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by wuyihao on 5/16/17.
 */
@Entity
public class Authorities {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    @NotNull
    String username;
    @NotNull
    String authority;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public Authorities() {
    }

    public Authorities(String username, String authority) {
        this.username = username;
        this.authority = authority;
    }
}
