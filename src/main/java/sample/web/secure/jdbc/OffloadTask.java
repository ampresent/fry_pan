package sample.web.secure.jdbc;

/**
 * Created by wuyihao on 4/22/17.
 */
public class OffloadTask {
    private String username;
    private String path;
    private String url;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public OffloadTask(String username, String path, String url) {
        this.username = username;
        this.path = path;
        this.url = url;
    }
}
