package sample.web.secure.jdbc.Service.Inter;

import sample.web.secure.jdbc.Domain.OfflineTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyihao on 4/29/17.
 */
public interface OfflineSer {
    public void append(String username, String path, String url);
    public void download();
    public void pop(String username, long [] ids);
    public void clear(String username);
    public List<OfflineTask> getAll(String username);
}
