package sample.web.secure.jdbc.Service.Inter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sample.web.secure.jdbc.Domain.Garbage;

/**
 * Created by wuyihao on 5/25/17.
 */
public interface GarbageSer {
    Page<Garbage> listGarbage(String username, Pageable p);
    void clearGarbages(String username);
    void restoreGarbages(String username, long[] ids);
}
