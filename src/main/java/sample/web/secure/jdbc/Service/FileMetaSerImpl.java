package sample.web.secure.jdbc.Service;

import org.apache.commons.io.FilenameUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import sample.web.secure.jdbc.Domain.FileMeta;
import sample.web.secure.jdbc.Domain.FileMetaRepository;
import sample.web.secure.jdbc.Domain.Garbage;
import sample.web.secure.jdbc.Service.Inter.FileMetaSer;
import sample.web.secure.jdbc.Service.Inter.FileUploadSer;
import sample.web.secure.jdbc.Service.Inter.SystemSer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import static org.apache.commons.io.FilenameUtils.getExtension;

/**
 * Created by wuyihao on 5/8/17.
 */
@Service
public class FileMetaSerImpl implements FileMetaSer {
    private FileMetaRepository fileMetaRepository;
    private FileUploadSer fileUploadSer;
    private SystemSer systemSer;
    private final static Set<String> videoExtension = new HashSet<String>(Arrays.asList(
            new String[] {"webm","mkv","flv","flv","vob","ogv","ogg","drc","gif","gifv","mng","avi","mov","qt","wmv","yuv","rm","rmvb","asf","amv","mp4","m4p","m4v","mpg","mp2","mpeg","mpe","mpv","mpg","m2v","m4v","svi","3gp","3g2","mxf","roq","nsv","flv","f4v","f4p","f4a","f4b"}));
    private final static Set<String> pictureExtension = new HashSet<String>(Arrays.asList(
            new String[] {"ani", "bmp", "cal", "fax", "gif", "img", "jbg", "jpe", "jpeg", "jpg", "mac", "pbm", "pcd", "pcx", "pct", "pgm", "png", "ppm", "psd", "ras", "tga", "tiff", "wmf" }));

    @Autowired
    public FileMetaSerImpl(FileMetaRepository fileMetaRepository, FileUploadSer fileUploadSer, SystemSer systemSer) {
        this.fileMetaRepository = fileMetaRepository;
        this.fileUploadSer = fileUploadSer;
        this.systemSer = systemSer;
    }

    @Override
    @Scheduled(cron = "30 * * * * *")
    public void genMetas() {
        if (!systemSer.lowPressure()) {
            return;
        }
        Iterable<FileMeta> fileMetas = fileMetaRepository.findByScannedAndGarbageIsNull(false);
        for (FileMeta fm : fileMetas) {
            if (!systemSer.lowPressure()) {
                break;
            }
            try {
                if (videoExtension.contains(getExtension(fm.getPath()).toLowerCase())) {
                    String local_path = fileUploadSer.dupTemp("/user/" + fm.getUsername() + "/" + fm.getPath());
                    File f = new File(local_path);
                    try {
                        FFmpegFrameGrabber g = new FFmpegFrameGrabber(f);
                        Java2DFrameConverter paintConverter = new Java2DFrameConverter();
                        g.start();
                        g.setFrameNumber(g.getLengthInFrames() / 2);
                        BufferedImage image = paintConverter.getBufferedImage(g.grabImage());
                        g.stop();
                        OutputStream os = fileUploadSer.putFileOutputStream("/thumb/" + fm.getId() + ".jpg");
                        ImageIO.write(image, "jpg", os);
                        os.close();
                    } catch (FrameGrabber.Exception ex) {
                        ex.printStackTrace();
                    }
                    fm.setType(FileMeta.FileT.VIDEO);
                    fm.setScanned(true);
                    fm.setNeedThumb(true);
                    fileMetaRepository.save(fm);
                } else if (pictureExtension.contains(getExtension(fm.getPath()).toLowerCase())) {
                    String local_path = fileUploadSer.dupTemp("/user/" + fm.getUsername() + "/" + fm.getPath());
                    File f = new File(local_path);
                    BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
                    image.createGraphics().drawImage(ImageIO.read(f).
                            getScaledInstance(64, 64, Image.SCALE_SMOOTH),0,0,null);

                    OutputStream os = fileUploadSer.putFileOutputStream("/thumb/" + fm.getId() + ".jpg");
                    ImageIO.write(image, "jpg", os);
                    os.close();
                    fm.setType(FileMeta.FileT.PICTURE);
                    fm.setScanned(true);
                    fm.setNeedThumb(true);
                    fileMetaRepository.save(fm);
                } else {
                    fm.setScanned(true);
                    fm.setNeedThumb(false);
                    fileMetaRepository.save(fm);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    @Async
    public void add(String username, String path) {
        path = FilenameUtils.normalize("/./" + path);
        try{
            InputStream in = fileUploadSer.getFileInputStream("/user/" + username + "/" + path);
            fileMetaRepository.save(new FileMeta(username, path, DigestUtils.md5DigestAsHex(in)));
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void updatePath(String username, String from, String to) {
        from = FilenameUtils.normalize("/./" + from);
        to = FilenameUtils.normalize("/./" + to);
        FileMeta fileMeta = fileMetaRepository.findOneByUsernameAndPathAndGarbageIsNull(username, from);
        fileMeta.setPath(to);
        fileMetaRepository.save(fileMeta);
    }

    @Override
    public FileMeta delete(String username, String path, boolean trash) throws Exception{
        path = FilenameUtils.normalize("/./" + path);
        if (trash) {
            FileMeta fileMeta = fileMetaRepository.findOneByUsernameAndPathAndGarbageIsNull(username, path);
            if (fileMeta == null || fileMeta.getGarbage() != null) {
                throw new Exception("Delete file failed");
            }
            Garbage garbage = new Garbage();
            fileMeta.setGarbage(garbage);
            garbage.setFileMeta(fileMeta);
            fileMetaRepository.save(fileMeta);
            return fileMeta;
        } else {
            fileMetaRepository.deleteByUsernameAndPathAndGarbageIsNull(username, path);
            return null;
        }
    }

    @Override
    public InputStream getThumbStream(String username, String path) {
        path = FilenameUtils.normalize("/./" + path);
        FileMeta fileMeta = fileMetaRepository.findByUsernameAndPathAndNeedThumbAndGarbageIsNull(username, path, true);
        if (fileMeta == null || !fileMeta.isNeedThumb()) {
            InputStream in = System.class.getResourceAsStream("/static/image/fbthumb/" + FilenameUtils.getExtension(path) + ".png");
            if (in == null) {
                return System.class.getResourceAsStream("/static/image/fbthumb/_blank.png");
            }
            return in;
        } else {
            return fileUploadSer.getFileInputStream("/thumb/" + fileMeta.getId() + ".jpg");
        }
    }

    @Override
    public FileMeta find(String username, String path) {
        path = FilenameUtils.normalize("/./" + path);
        return fileMetaRepository.findOneByUsernameAndPathAndGarbageIsNull(username, path);
    }

    /*
    @Override
    public FileMeta find(long id) {
        return fileMetaRepository.findOne(id);
    }
    */

    @Override
    public boolean exist(String username, String path) {
        path = FilenameUtils.normalize("/./" + path);
        return fileMetaRepository.existsByUsernameAndPathAndGarbageIsNull(username, path);
    }

    @Override
    public Page<FileMeta> findByUsernameAndType(String username, FileMeta.FileT fileT, Pageable p) {
        Page<FileMeta> result = fileMetaRepository.findByUsernameAndTypeAndGarbageIsNull(username, fileT, p);
        return result;
    }

    @Override
    public FileMeta findByHash(String hash) {
        return fileMetaRepository.findByHash(hash);
    }

    @Override
    public FileMeta fastUpload(String username, FileMeta fileMeta, String path) {
        path = FilenameUtils.normalize("/./" + path);
        FileMeta newFileMeta = new FileMeta(fileMeta);

        //String path = fileMeta.getPath();
        //String newPath = "/user/" + username + path.substring(path.indexOf("/", path.indexOf("/", path.indexOf("/")+1)+1));
        newFileMeta.setShare(null);
        newFileMeta.setGarbage(null);
        newFileMeta.setUsername(username);
        newFileMeta.setPath(path);
        //newFileMeta.setType(fileMeta.getType());
        //newFileMeta.setNeedThumb(fileMeta.isNeedThumb());
        //newFileMeta.setHash(fileMeta.getHash());
        //newFileMeta.setScanned(fileMeta.isScanned());

        fileMetaRepository.save(newFileMeta);

        return newFileMeta;
    }


    public void unGarbage(FileMeta fileMeta) {
        fileMeta.setGarbage(null);
        fileMetaRepository.save(fileMeta);
    }
    /*
    @Override
    public FileMeta[] listFiles(String username, String path, Pageable p) {
        return fileMetaRepository.findByUsernameAndPathStartingWithAndGarbageIsNull(username, path, p);
    }

    @Override
    public FileMeta upload(String fp, String username, String path, String hash) throws IOException{
        if (fileMetaRepository.existsByUsernameAndPathAndGarbageIsNull(username, path)) {
            throw new IOException("File exists");
        } else {
            FileMeta fileMeta = fileMetaRepository.findByHash(hash);
            if (fileMeta != null) {
                return fastUpload(username, fileMeta, path);
            }
            FileMeta newFileMeta = new FileMeta(fp, username, path, hash, false);
            fileMetaRepository.save(newFileMeta);
            return newFileMeta;
        }
    }
    */

    /*
    @Override
    public String getFpById(long id) {
        FileMeta fileMeta = fileMetaRepository.findOne(id);
        return fileMeta.getFp();
    }

    @Override
    public void mkdir(String path) {
        int lastSlash = path.lastIndexOf("/");
        String current = path.substring(lastSlash + 1);
        String newFolder = path.substring(0, lastSlas)
    }
    */
}
