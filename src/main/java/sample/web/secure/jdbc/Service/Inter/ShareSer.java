package sample.web.secure.jdbc.Service.Inter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sample.web.secure.jdbc.Domain.FileMeta;
import sample.web.secure.jdbc.Domain.Share;

import java.io.OutputStream;
import java.util.Date;

/**
 * Created by wuyihao on 5/6/17.
 */
public interface ShareSer {
    FileMeta getShare(String username, String path) throws Exception;
    //String shareName(String username, long id);
    //String downloadShare(String username, long id, OutputStream os);
    Page<Share> listShare(String username, Pageable p);
    void deleteShare(String username, long[] id);
    Page<Share> hot(String username, Pageable pageable);
}
