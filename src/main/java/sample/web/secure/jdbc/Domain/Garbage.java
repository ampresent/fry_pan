package sample.web.secure.jdbc.Domain;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wuyihao on 5/6/17.
 */
@Entity
public class Garbage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private Date erasetime;
    @NotNull
    private Date deletetime;
    @OneToOne
    @JoinColumn(name = "garbage_id")
    private FileMeta fileMeta;
    public Garbage() {
        Calendar c = Calendar.getInstance();
        deletetime = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 30);
        erasetime = c.getTime();
    }

    public Date getErasetime() {
        return erasetime;
    }

    public void setErasetime(Date erasetime) {
        this.erasetime = erasetime;
    }

    public Date getDeletetime() {
        return deletetime;
    }

    public void setDeletetime(Date deletetime) {
        this.deletetime = deletetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FileMeta getFileMeta() {
        return fileMeta;
    }

    public void setFileMeta(FileMeta fileMeta) {
        this.fileMeta = fileMeta;
    }
}
