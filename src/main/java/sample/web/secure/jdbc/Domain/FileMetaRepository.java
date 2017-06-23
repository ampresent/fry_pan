package sample.web.secure.jdbc.Domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by wuyihao on 5/8/17.
 */
@Transactional
public interface FileMetaRepository extends CrudRepository<FileMeta, Long> {
    Iterable<FileMeta> findByScannedAndGarbageIsNull(boolean scanned);
    FileMeta findByUsernameAndPathAndNeedThumbAndGarbageIsNull(String username, String path, boolean need_thumb);
    //Page<FileMeta> findByUsernameAndPathAndGarbageIsNull(String username, String path, Pageable p);
    FileMeta findOneByUsernameAndPathAndGarbageIsNull(String username, String path);
    //Page<FileMeta> findByTypeAndGarbageIsNull(FileMeta.FileT fileT, Pageable p);
    void deleteByUsernameAndPathAndGarbageIsNull(String username, String path);
    boolean existsByUsernameAndPathAndGarbageIsNull(String username, String path);
    FileMeta findByHash(String hash);
    //Page<FileMeta> findByUsernameAndPathStartingWithAndGarbageIsNull(String username, String path, Pageable p);
    //Page<FileMeta> findByUsernameTypeAndGarbageIsNull(String username, FileMeta.FileT fileT, Pageable p);
    Page<FileMeta> findByUsernameAndTypeAndGarbageIsNull(String username, FileMeta.FileT fileT, Pageable p);
    FileMeta[] findByUsernameAndGarbageNotNull(String username);
}
