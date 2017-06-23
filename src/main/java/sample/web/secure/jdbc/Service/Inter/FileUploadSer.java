package sample.web.secure.jdbc.Service.Inter;

import org.apache.hadoop.fs.FileStatus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wuyihao on 4/29/17.
 */
public interface FileUploadSer {
    void putFile(String path, InputStream is);
    String getFile(String path, OutputStream os);
    InputStream getFileInputStream(String path);
    OutputStream putFileOutputStream(String path);
    FileStatus[] listFiles(String path);
    void mkdir(String path);
    boolean exist(String path);
    boolean bigFile(String path);
    String dupTemp(String path) throws IOException;
    void deleteFile(String path);
    void moveFile(String from, String to);
    void hardlink(String from, String to);
}
