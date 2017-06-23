package sample.web.secure.jdbc.Domain;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

/**
 * Created by wuyihao on 5/8/17.
 */
@Entity
public class FileMeta {
    public enum FileT {
        VIDEO, PICTURE, DOCUMENT, OTHERS
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @NotNull
    protected String path;

    @NotNull
    protected String username;

    @NotNull
    protected String hash;

    @NotNull
    @Type(type="yes_no")
    protected boolean needThumb;

    @NotNull
    @Type(type="yes_no")
    protected boolean scanned;

    @OneToOne(cascade = CascadeType.ALL)
    protected Garbage garbage;

    @Enumerated(EnumType.ORDINAL)
    private FileT type;

    @OneToOne(cascade = CascadeType.ALL)
    private Share share;

    public FileMeta() { }

    public FileMeta(String username, String path, String hash) {
        this.path = path;
        this.username = username;
        this.hash = hash;
        this.scanned = false;
        this.needThumb = false;
        this.type = FileT.OTHERS;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public FileMeta(FileMeta filemeta) {
        this.path = filemeta.getPath();
        this.username = filemeta.getUsername();
        this.hash = filemeta.getHash();
        this.scanned = filemeta.isScanned();
        this.needThumb = filemeta.isNeedThumb();
        this.type = filemeta.type;
        this.share = filemeta.share;
        this.garbage = filemeta.garbage;
    }

    public FileT getType() {
        return type;
    }

    public void setType(FileT type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean isNeedThumb() {
        return needThumb;
    }

    public void setNeedThumb(boolean needThumb) {
        this.needThumb = needThumb;
    }

    public boolean isScanned() {
        return scanned;
    }

    public void setScanned(boolean scanned) {
        this.scanned = scanned;
    }

    public Garbage getGarbage() {
        return garbage;
    }

    public void setGarbage(Garbage garbage) {
        this.garbage = garbage;
    }

}
