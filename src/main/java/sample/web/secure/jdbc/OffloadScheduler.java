package sample.web.secure.jdbc;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.http.HttpRequestFactory;
import org.h2.util.Task;
import org.springframework.http.HttpRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyihao on 4/22/17.
 */

// Schedule downloader to save bandwidth

@Service
public class OffloadScheduler {

    //private static final SimpleDataFormat dataFormat = new SimpleDateFormat("");

    private List<OffloadTask> offloadQueue = new ArrayList<OffloadTask>();

    boolean lowPressure() {
        return true;
    }

    @Scheduled(fixedRate = 5000)
    public void download() {
        if (!lowPressure() || offloadQueue.isEmpty()) {
            return;
        }
        // I should use a combine strategy
        org.apache.hadoop.conf.Configuration config = new org.apache.hadoop.conf.Configuration();
        config.addResource(new Path("/usr/lib/hadoop/etc/hadoop/core-site.xml"));
        config.addResource(new Path("/usr/lib/hadoop/etc/hadoop/hdfs-site.xml"));
        config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        config.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());

        try {
            FileSystem fileSystem = FileSystem.get(config);
            for (OffloadTask task : offloadQueue) {
                Path p = new Path("hdfs://localhost:9000/user/" + task.getUsername() + "/" + task.getPath());
                OutputStream os = fileSystem.create(p);

                URL url = new URL(task.getUrl());
                InputStream is = url.openStream();

                org.apache.commons.io.IOUtils.copy(is, os);

                is.close();
                os.close();
                fileSystem.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void append(String username, String path, String url) {
        offloadQueue.add(new OffloadTask(username, path, url));
    }
}
