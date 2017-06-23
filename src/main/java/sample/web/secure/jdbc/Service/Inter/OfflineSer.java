package sample.web.secure.jdbc.Service.Inter;

import sample.web.secure.jdbc.Domain.OfflineTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyihao on 4/29/17.
 */
public interface OfflineSer {
    void append(String username, String path, String url);
    void download();
    void pop(String username, long [] ids);
    void clear(String username);
    List<OfflineTask> getAll(String username);
}
