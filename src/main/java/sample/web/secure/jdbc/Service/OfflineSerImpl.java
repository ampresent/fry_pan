package sample.web.secure.jdbc.Service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Domain.OfflineTask;
import sample.web.secure.jdbc.Domain.OfflineTaskRepository;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;
import sample.web.secure.jdbc.Service.Inter.OfflineSer;
import sample.web.secure.jdbc.Service.Inter.SystemSer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyihao on 4/22/17.
 */

// Schedule downloader to save bandwidth

@Service
public class OfflineSerImpl implements OfflineSer {

    private SystemSer systemSer;
    private FileUploadSer fileUploadSer;
    private ArrayList<OfflineTask> offlineQueue;
    private OfflineTaskRepository offlineTaskDao;

    @Autowired
    public OfflineSerImpl(SystemSer systemSer, FileUploadSer fileUploadSer, OfflineTaskRepository offlineTaskDao) {
        this.systemSer = systemSer;
        this.fileUploadSer = fileUploadSer;
        this.offlineTaskDao = offlineTaskDao;
    }

    @Scheduled(cron = "0 * * * * *")
    public void download() {
        if (!systemSer.lowPressure() || offlineTaskDao.count() == 0) {
            return;
        }
        for (OfflineTask task : (Iterable<OfflineTask>)offlineTaskDao.findAll()) {
            if (!systemSer.lowPressure()) {
                break;
            }
            try {
                URL url = new URL(task.getUrl());
                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
                httpcon.addRequestProperty("User-Agent", "Mozilla/4.0");
                InputStream is = httpcon.getInputStream();
                String filename = FilenameUtils.getName(url.getPath());
                if (filename == "" || filename == "/")
                    filename = "index.html";
                fileUploadSer.putFile("/user/" + task.getUsername() + "/" + task.getPath() + "/" + filename, is);
                is.close();
                offlineTaskDao.delete(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void append(String username, String path, String url) {
        offlineTaskDao.save(new OfflineTask(username, path, url));
    }

    public void pop(String username, long ids[]) {
        for (long id: ids) {
            offlineTaskDao.deleteByUsernameAndId(username, id);
        }
    }

    public void clear(String username) {
        offlineTaskDao.deleteByUsername(username);
    }

    public List<OfflineTask> getAll(String username) {
        return offlineTaskDao.findByUsername(username);
    }
}
