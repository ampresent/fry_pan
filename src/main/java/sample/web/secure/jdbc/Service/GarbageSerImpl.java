package sample.web.secure.jdbc.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Domain.FileMeta;
import sample.web.secure.jdbc.Domain.Garbage;
import sample.web.secure.jdbc.Domain.GarbageRepository;
import sample.web.secure.jdbc.Service.Inter.FileMetaSer;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;
import sample.web.secure.jdbc.Service.Inter.GarbageSer;

/**
 * Created by wuyihao on 5/25/17.
 */

@Service
public class GarbageSerImpl implements GarbageSer {
    @Autowired
    GarbageRepository garbageRepository;
    @Autowired
    FileUploadSer fileUploadSer;
    @Autowired
    FileMetaSer fileMetaSer;

    public Page<Garbage> listGarbage(String username, Pageable p) {
        return garbageRepository.findByFileMeta_username(username, p);
    }

    public void clearGarbages(String username) {
        for (Garbage garbage : garbageRepository.findByFileMeta_username(username)) {
            FileMeta fileMeta = garbage.getFileMeta();
            fileUploadSer.deleteFile("/trash/" + fileMeta.getUsername() + "/" + garbage.getId());
            garbageRepository.delete(garbage);
        }
    }
    public void restoreGarbages(String username, long[] ids) {
        for (long id : ids) {
            Garbage garbage = garbageRepository.findOne(id);
            FileMeta fileMeta = garbage.getFileMeta();
            if (!fileMeta.getUsername().equals(username)) {
                continue;
            }
            fileUploadSer.moveFile("/trash/" + fileMeta.getUsername() + "/" + garbage.getId(), fileMeta.getPath());
            fileMetaSer.unGarbage(fileMeta);
        }
    }

}
