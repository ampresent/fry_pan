package sample.web.secure.jdbc.Service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Domain.*;
import sample.web.secure.jdbc.Service.Inter.FileMetaSer;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;
import sample.web.secure.jdbc.Service.Inter.FriendsSer;
import sample.web.secure.jdbc.Service.Inter.ShareSer;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wuyihao on 5/6/17.
 */
@Service
public class ShareSerImpl implements ShareSer {

    private FileMetaRepository fileMetaRepository;
    private ShareRepository shareRepository;
    private FileUploadSer fileUploadSer;
    private FriendsSer friendsSer;
    private FileMetaSer fileMetaSer;
    private UserRepository userRepository;

    @Autowired
    public ShareSerImpl(FileMetaRepository fileMetaRepository, ShareRepository shareRepository, FileUploadSer fileUploadSer, FriendsSer friendsSer, FileMetaSer fileMetaSer, UserRepository userRepository) {
        this.fileMetaRepository = fileMetaRepository;
        this.shareRepository = shareRepository;
        this.fileUploadSer = fileUploadSer;
        this.friendsSer = friendsSer;
        this.fileMetaSer = fileMetaSer;
        this.userRepository = userRepository;
    }

    public FileMeta getShare(String username, String path) throws Exception{
        path = FilenameUtils.normalize("/./" + path);
        FileMeta fileMeta = fileMetaSer.find(username, path);
        if (fileMeta == null || fileMeta.getShare() != null) {
            throw new Exception("Failed to create Share!");
        }
        /*
        Share share = new Share(fileMeta.getId(), fileMeta.getUsername());
        //Share share = new Share(username, path);
        */
        //fileMeta.save(fileMeta);
        Share share = new Share();
        fileMeta.setShare(share);
        share.setFileMeta(fileMeta);
        fileMetaRepository.save(fileMeta);
        return fileMeta;
    }

    /*
    public String shareName(String username, long id) {
        Share share = shareRepository.findOne(id);
        userRepository.findOne(share)
        if (friendsSer.isFriends(share.getUsername(), username)) {
            return FilenameUtils.getName(share.getPath());
        } else {
            throw new AccessDeniedException(username + "has no authority to access share: " + String.valueOf(id));
        }
    }
    */

    /*
    public String downloadShare(String username, long id, OutputStream os) {
        FileMeta fileMeta = fileMetaSer.find(id);
        Share share = shareRepository.findOne(id);
        if (friendsSer.isFriends(fileMeta.getUsername(), username)) {
            return fileUploadSer.getFile(fileMeta.getPath(), os);
        } else {
            throw new AccessDeniedException(username + "has no authority to access share: " + String.valueOf(id));
        }
    }
    */

    public Page<Share> listShare(String username, Pageable p) {
        return shareRepository.findByFileMeta_Username(username, p);
    }

    public void deleteShare(String username, long[] ids) {
        shareRepository.deleteByFileMeta_UsernameAndIdIn(username, ids);
    }

    public Page<Share> hot(String username, Pageable pageable) {
        String[] followings = friendsSer.getFollowing(username);
        return shareRepository.findByFileMeta_UsernameInOrderByLikesDesc(followings, pageable);
    }
}
