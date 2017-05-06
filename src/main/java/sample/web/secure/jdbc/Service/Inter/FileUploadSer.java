package sample.web.secure.jdbc.Service.Inter;

import org.apache.hadoop.fs.FileStatus;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wuyihao on 4/29/17.
 */
public interface FileUploadSer {
    void putFile(String path, InputStream is);
    String getFile(String path, OutputStream os);
    FileStatus[] listFiles(String path);
    void mkdir(String path);
}
