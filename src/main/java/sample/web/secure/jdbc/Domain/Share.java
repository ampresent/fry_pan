package sample.web.secure.jdbc.Domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import sample.web.secure.jdbc.Service.Inter.FileMetaSer;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wuyihao on 5/6/17.
 */
@Entity
public class Share { //extends FileMeta {
    @Id
    @Column(name = "FILEMETA_ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private Date expire;
    @NotNull
    private Date createtime;
    @NotNull
    private long likes;
    @OneToOne
    @JoinColumn(name = "share_filemeta_id")
    private FileMeta fileMeta;

    /*
    @PrePersist
    public void onCreate() {
        Calendar c = Calendar.getInstance();
        createtime = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 30);
        expire = c.getTime();

        // Do I really need this ?????????
//        if (!this.isScanned()) {
//            // Safe to execute genMetas
//            // Because @Scheduled annotation doesn't create thread
//            // As long as not providing pool-size attribute
//            fileMetaSer.genMetas();
//        }
//        if (this.isNeedThumb()) {
//            thumb = this.getThumb();
//        }
    }
    */

    public Share() {
        Calendar c = Calendar.getInstance();
        createtime = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 30);
        expire = c.getTime();
        likes = 0;
    }

    /*
    public Share(long id, String username) {
        this.id = id;
        this.username = username;
        likes = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    */


    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    /*
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    */

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public FileMeta getFileMeta() {
        return fileMeta;
    }

    public void setFileMeta(FileMeta fileMeta) {
        this.fileMeta = fileMeta;
    }
}
