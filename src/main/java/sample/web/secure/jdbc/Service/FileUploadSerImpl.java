package sample.web.secure.jdbc.Service;

import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wuyihao on 4/29/17.
 */
@Service
public class FileUploadSerImpl implements FileUploadSer {
    private org.apache.hadoop.conf.Configuration config;
    private FileSystem fileSystem;

    @Autowired
    public FileUploadSerImpl() {
        config = new org.apache.hadoop.conf.Configuration();
        config.addResource(new Path("/usr/lib/hadoop/etc/hadoop/core-site.xml"));
        config.addResource(new Path("/usr/lib/hadoop/etc/hadoop/hdfs-site.xml"));
        config.set("fs.hdfs.impl", org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        config.set("fs.file.impl",org.apache.hadoop.fs.LocalFileSystem.class.getName());
        try {
            fileSystem = FileSystem.get(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() {
        try {
            fileSystem.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileStatus[] listFiles(String path) {
        try {
            Path p = new Path("hdfs://localhost:9000" + path);
            return fileSystem.listStatus(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void mkdir(String path) {
        try {
            Path p = new Path("hdfs://localhost:9000" + path);
            fileSystem.mkdirs(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void putFile(String path, InputStream is) {
        try {
            Path p = new Path("hdfs://localhost:9000" + path);
            if (fileSystem.exists(p)) {
                throw new IOException();
            }
            OutputStream os = fileSystem.create(p);
            org.apache.commons.io.IOUtils.copy(is, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFile(String path, OutputStream os) {
        try {
            Path p = new Path("hdfs://localhost:9000" + path);
            if (!fileSystem.exists(p)){
                throw new IOException();
            }

            InputStream is = fileSystem.open(p);
            org.apache.commons.io.IOUtils.copy(is, os);
            is.close();
            return p.getName();
        } catch (IOException ex){
            throw new RuntimeException("IOError writing file to output stream.");
        }
    }

    @Override
    public InputStream getFileInputStream(String path) {
        try {
            Path p = new Path("hdfs://localhost:9000" + path);
            if (!fileSystem.exists(p)) {
                throw new IOException();
            }
            InputStream is = fileSystem.open(p);
            return is;
        } catch (IOException ex) {
            throw new RuntimeException("IOError writing file to output stream.");
        }
    }

    @Override
    public OutputStream putFileOutputStream(String path) {
        try {
            Path p = new Path("hdfs://localhost:9000" + path);
            if (fileSystem.exists(p)) {
                throw new IOException();
            }
            OutputStream os = fileSystem.create(p);
            return os;
        } catch (IOException e) {
            throw new RuntimeException("IOError reading file from input stream.");
        }
    }
    @Override
    public boolean exist(String path) {
        Path p = new Path("hdfs://localhost:9000" + path);
        try {
            return fileSystem.exists(p);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void deleteFile(String path) {
        Path p = new Path("hdfs://localhost:9000" + path);
        try {
            fileSystem.delete(p, false);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void moveFile(String from, String to) {
        Path f = new Path("hdfs://localhost:9000" + from);
        Path t = new Path("hdfs://localhost:9000" + to);
        try {
            fileSystem.rename(f, t);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean bigFile(String path) {
        return true;
    }

    @Override
    public String dupTemp(String path) throws IOException{
        Path p = new Path("hdfs://localhost:9000" + path);
        String local_path = File.createTempFile("frytemp", "." + FilenameUtils.getExtension(path)).getAbsolutePath();
        fileSystem.copyToLocalFile(p, new Path(local_path));
        return local_path;
    }

    @Override
    public void hardlink(String from, String to) {
        Path f = new Path("hdfs://localhost:9000" + from);
        Path t = new Path("hdfs://localhost:9000" + to);
        try {
            fileSystem.createSymlink(f, t, true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
