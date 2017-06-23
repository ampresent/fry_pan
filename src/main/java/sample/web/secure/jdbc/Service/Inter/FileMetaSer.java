package sample.web.secure.jdbc.Service.Inter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import sample.web.secure.jdbc.Domain.FileMeta;
import sample.web.secure.jdbc.Domain.Garbage;

import java.io.InputStream;

/**
 * Created by wuyihao on 5/8/17.
 */
public interface FileMetaSer {
    @Async
    void add(String username, String path);
    FileMeta delete(String username, String path, boolean trash) throws Exception;
    void updatePath(String username, String from, String to);
    void genMetas();
    InputStream getThumbStream(String username, String path);
    FileMeta find(String username, String path);
    boolean exist(String username, String path);
    //FileMeta find(long id);
    //Page<FileMeta> findByType(FileMeta.FileT fileT, Pageable p);
    FileMeta findByHash(String hash);
    FileMeta fastUpload(String username, FileMeta fileMeta, String path);
    Page<FileMeta> findByUsernameAndType(String username, FileMeta.FileT fileT, Pageable p);
    void unGarbage(FileMeta fileMeta);
}
