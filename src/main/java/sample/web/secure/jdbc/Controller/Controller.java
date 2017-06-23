/*
 * Copyright 2012-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.web.secure.jdbc.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.context.SecurityContextHolder;
import sample.web.secure.jdbc.Domain.FileMeta;
import sample.web.secure.jdbc.Domain.Garbage;
import sample.web.secure.jdbc.Domain.UserInfo;
import sample.web.secure.jdbc.Service.Inter.*;

@SpringBootApplication
@org.springframework.stereotype.Controller
public class Controller extends WebMvcConfigurerAdapter {

    private FileUploadSer fileUploadSer;
    private OfflineSer offlineSer;
    private FriendsSer friendsSer;
    private ShareSer shareSer;
    private FileMetaSer fileMetaSer;
    private UserSer userSer;
    private GarbageSer garbageSer;

    @Autowired
    public Controller(FileUploadSer fileUploadSer, OfflineSer offlineSer, FriendsSer friendsSer, ShareSer shareSer,
                      FileMetaSer fileMetaSer, UserSer userSer, GarbageSer garbageSer) {
        this.fileUploadSer = fileUploadSer;
        this.offlineSer = offlineSer;
        this.friendsSer = friendsSer;
        this.shareSer = shareSer;
        this.fileMetaSer = fileMetaSer;
        this.userSer = userSer;
        this.garbageSer = garbageSer;
    }

    @RequestMapping(value = "/folder/create", method = RequestMethod.POST)
    public String createFolder(@RequestParam String current, @RequestParam String folder) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String path = "/user/" + user.getUsername() + "/" + current + "/" + folder ;
        fileUploadSer.mkdir(path);
        return "home";
    }

    @RequestMapping(value = "/file/delete", method = RequestMethod.POST)
    public String deleteFile(@RequestParam String path) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //fileUploadSer.deleteFile("/user/" + user.getUsername() + "/" + path);
        try {
            FileMeta garbage = fileMetaSer.delete(user.getUsername(), path, true);
            fileUploadSer.moveFile("/user/" + user.getUsername() + "/" + path, "/trash/" + user.getUsername() + "/" + garbage.getGarbage().getId());
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return "home";
    }

    @RequestMapping(value = "/file/move", method = RequestMethod.POST)
    public String moveFile(@RequestParam String from, @RequestParam String to) {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        fileUploadSer.moveFile("/user/" + username + "/" + from, "/user/" + username + "/" + to);
        fileMetaSer.updatePath(username, from, to);
        return "home";
    }

    @RequestMapping(value = "/file/upload", method = RequestMethod.POST)
    public String uploadFile(@RequestParam String current, @RequestParam MultipartFile file, @RequestParam String hash) {
        if (file.isEmpty()){
            //return new ResponseEntity("Please select a file!", HttpStatus.OK);
            return "error";
        }
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String path = current + "/" + file.getOriginalFilename();
        if (fileMetaSer.exist(user.getUsername(), path)) {
            return "error";
        }
        String disk_path = "/user/" + user.getUsername() + "/" + path;
        FileMeta fileMeta = fileMetaSer.findByHash(hash);
        if (fileMeta != null) {
            FileMeta newFileMeta = fileMetaSer.fastUpload(user.getUsername(), fileMeta, path);
            fileUploadSer.hardlink("/user/" + fileMeta.getUsername() + "/" + fileMeta.getPath(), disk_path);
            return "home";
        }
        try {
            InputStream in = file.getInputStream();
            fileUploadSer.putFile(disk_path, in);
            in.close();
            if (fileUploadSer.bigFile(disk_path)) {
                fileMetaSer.add(user.getUsername(), path);
            }
        } catch (IOException ex) {
            //return new ResponseEntity("Failed to upload!", HttpStatus.OK);
            return "error";
        }
        //return new ResponseEntity("Successfully uploaded - " + uploadFile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);
        return "home";
    }

    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public String share(@RequestParam(required = false) Integer page, Map<String, Object> model) {
        if (page == null) {
            page = 1;
        }
        page -= 1;
        PageRequest pageRequest = new PageRequest(  page , 8);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("shares", shareSer.listShare(user.getUsername(), pageRequest));
        model.put("username", user.getUsername());
        model.put("page", page+1);
        return "share";
    }

    @RequestMapping(value = "/share/create", method = RequestMethod.POST)
    public String getShare(@RequestParam String path) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (fileUploadSer.exist("/user/" + user.getUsername() + "/" + path)) {
            try {
                shareSer.getShare(user.getUsername(), path);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "share";
        }
        return "share";
    }

    @RequestMapping(value = "/file/access", method = RequestMethod.GET)
    public void download(@RequestParam(value="file", required = false) String file, HttpServletResponse response) {
        try {
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            file = "/user/" + user.getUsername() + "/" + file;
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary");
            String filename = FilenameUtils.getName(file);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            fileUploadSer.getFile(file, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex){
            throw new RuntimeException("IOError writing file to output stream.");
        }
    }

    @RequestMapping(value = "/secure/access", method = RequestMethod.GET)
    public void secureDownload(@RequestParam(value="file", required = false) String file, HttpServletResponse response) {
        try {
            User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            file = "/secure/" + user.getUsername() + "/" + file;
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary");
            String filename = FilenameUtils.getName(file);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            fileUploadSer.getFile(file, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ex){
            throw new RuntimeException("IOError writing file to output stream.");
        }
    }

    @GetMapping("/trash")
    public String trash(@RequestParam(required = false) Integer page, Map<String, Object> model) {
        if (page == null) {
            page = 1;
        }
        page -= 1;
        PageRequest pageRequest = new PageRequest(page, 8);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("username", user.getUsername());
        model.put("garbages", garbageSer.listGarbage(user.getUsername(), pageRequest));
        model.put("page", page+1);
        return "trash";
    }

    @RequestMapping(value="/trash/restore", method=RequestMethod.POST)
    public String restoreGarbages(@RequestParam long[] ids) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        garbageSer.restoreGarbages(user.getUsername(), ids);
        return "share";
    }

    @RequestMapping(value="/trash/clear", method=RequestMethod.GET)
    public String clearGarbages() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        garbageSer.clearGarbages(user.getUsername());
        return "share";
    }

    @GetMapping("/")
    public String root(Map<String, Object> model) {
        return home("/", model);
    }

    @GetMapping("/disk")
    public String disk(Map<String, Object> model) { return home("/", model); }

	@RequestMapping(value="/disk/access", method=RequestMethod.GET)
    public String home(@RequestParam(value="path", required = false) String path, Map<String, Object> model) {

        if (path == null) {
            path = "";
        }
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("username", user.getUsername());
        FileStatus[] fss = fileUploadSer.listFiles("/user/" +user.getUsername() + "/" + path);
        model.put("files", fss);
        return "home";
    }

    @RequestMapping(value="/explore", method=RequestMethod.GET)
    public String explore(Map<String, Object> model) {
        Pageable first5 = new PageRequest(  0 , 5);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("figures", friendsSer.famousUser(first5));
        model.put("shares", shareSer.hot(user.getUsername(), first5));
        model.put("username", user.getUsername());
        return "explore";
    }

    @RequestMapping(value="/share/delete", method=RequestMethod.POST)
    public String deleteShare(@RequestParam long[] ids) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        shareSer.deleteShare(user.getUsername(), ids);
        return "share";
    }

    @RequestMapping(value="/offline/download", method=RequestMethod.POST)
    public String startOffline(@RequestParam String current, @RequestParam String url) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        offlineSer.append(user.getUsername(), current, url);
        return "home";
    }

    @RequestMapping(value="/offline/pop", method=RequestMethod.POST)
    public String popOffline(@RequestParam long[] ids) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        offlineSer.pop(user.getUsername(), ids);
        return "offline";
    }

    @RequestMapping(value="/offline/clear", method=RequestMethod.GET)
    public String clearOffline() {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        offlineSer.clear(user.getUsername());
        return "offline";
    }

    @RequestMapping(value="/offline", method=RequestMethod.GET)
    public String offline(Map<String, Object> model) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("tasks", offlineSer.getAll(user.getUsername()));
        model.put("username", user.getUsername());
        return "offline";
    }

    @ResponseBody
    @RequestMapping(value="/thumb/get", method=RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getThumb(@RequestParam(required = false) String file) {
        String username = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        try{
            InputStream thumbStream = fileMetaSer.getThumbStream(username, file);
            byte[] bytes = IOUtils.toByteArray(thumbStream);
            thumbStream.close();
            return bytes;
        } catch (IOException ex) {
            throw new RuntimeException("Expected exception in controller");
        }
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLogin(Model model) {
        UserInfo userInfo = new UserInfo();
        model.addAttribute("user", userInfo);
        return "login";
    }

    /*
    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegister() {
        return "register";
    }
    */

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(UserInfo userInfo) {
        userSer.register(userInfo);
        fileUploadSer.mkdir("/user/" + userInfo.getUsername());
        fileUploadSer.mkdir("/trash/" + userInfo.getUsername());
        fileUploadSer.mkdir("/secure/" + userInfo.getUsername());
        return "login";
    }

    @RequestMapping(value = "/friend/follow", method = RequestMethod.GET)
    public String follow(@RequestParam String username) {
        String follower = ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        friendsSer.follow(follower, username);
        return "user";
    }

    @RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
    public String user(@PathVariable String username, @RequestParam(required = false) Integer page, Map<String, Object> model) {
        if (page == null) {
            page = 1;
        }
        page -= 1;
        PageRequest pageRequest = new PageRequest(page, 8);
        model.put("user", userSer.find(username));
        model.put("shares", shareSer.listShare(username, pageRequest));
        model.put("page", page+1);
        return "user";
    }

    @RequestMapping(value = "/videos", method = RequestMethod.GET)
    public String videos(@RequestParam(required = false) Integer page, Map<String, Object> model) {
        if (page == null) {
            page = 1;
        }
        page = page - 1;
        Pageable p = new PageRequest(page, 8);
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("files", fileMetaSer.findByUsernameAndType(user.getUsername(), FileMeta.FileT.VIDEO, p));
        model.put("username", user.getUsername());
        model.put("page", page+1);
        return "classified";
    }

    @RequestMapping(value = "/secure/upload", method = RequestMethod.POST)
    public String secureUpload(@RequestParam String filename, @RequestParam String file, @RequestParam String hash, @RequestParam(required = false) String plain, @RequestParam(required = false) String enc) {
        if (file.isEmpty()){
            //return new ResponseEntity("Please select a file!", HttpStatus.OK);
            return "error";
        }
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            if (plain != null && enc != null)
                userSer.secZoneInit(user.getUsername(), plain, enc);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }

        String disk_path = "/secure/" + user.getUsername() + "/" + filename;
        try {
            InputStream in = new ByteArrayInputStream(file.getBytes());
            fileUploadSer.putFile(disk_path, in);
            in.close();
        } catch (IOException ex) {
            return "error";
        }
        return "secure";
    }

    @RequestMapping(value = "/secure", method = RequestMethod.GET)
    public String secure(Map<String, Object> model) {
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.put("username", user.getUsername());
        FileStatus[] fss = fileUploadSer.listFiles("/secure/" +user.getUsername());
        model.put("files", fss);
        UserInfo userInfo = userSer.find(user.getUsername());
        model.put("enc", userInfo.getEnc());
        model.put("plain", userInfo.getPlain());
        return "secure";
    }

	@RequestMapping("/foo")
	public String foo() {
		throw new RuntimeException("Expected exception in controller");
	}
}
